package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.configuration.jwt.JwtUtil;
import com.uis.schedule.backend.persistence.entity.PasswordResetTokenEntity;
import com.uis.schedule.backend.persistence.entity.RefreshTokenEntity;
import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.PasswordResetTokenRepository;
import com.uis.schedule.backend.persistence.repository.RefreshTokenRepository;
import com.uis.schedule.backend.persistence.repository.RevokedTokenRepository;
import com.uis.schedule.backend.persistence.repository.RoleRepository;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.CreateUserRequest;
import com.uis.schedule.backend.presentation.dto.UpdateUserRequest;
import com.uis.schedule.backend.presentation.dto.UserResponse;
import com.uis.schedule.backend.service.exception.InvalidSessionTokenException;
import com.uis.schedule.backend.service.exception.ProtectedRoleException;
import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.util.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String DELETE_ADMIN_MESSAGE =
            "Security protection: Users with ADMINISTRATOR role cannot be deleted.";
    private static final String DISABLE_ADMIN_MESSAGE =
            "Security protection: Users with ADMINISTRATOR role cannot be disabled.";
    private static final String STRIP_ADMIN_MESSAGE =
            "Security protection: Users with ADMINISTRATOR role cannot be stripped of that role.";

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private TokenGenerator tokenGenerator;
    @Mock
    private EmailService emailService;
    @Mock
    private UserService self;
    @Mock
    private RevokedTokenRepository revokedTokenRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                roleRepository,
                authenticationManager,
                jwtUtil,
                passwordEncoder,
                passwordResetTokenRepository,
                tokenGenerator,
                emailService,
                self,
                revokedTokenRepository,
                refreshTokenRepository);
    }

    // ─── Protección de administradores ───────────────

    @Test
    void deleteUser_administrator_forbidden() {
        UUID userId = UUID.randomUUID();
        UserEntity admin = enabledUser(userId, role("ADMINISTRATOR"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));

        ProtectedRoleException ex = assertThrows(ProtectedRoleException.class,
                () -> userService.deleteUser(userId));

        assertEquals(DELETE_ADMIN_MESSAGE, ex.getMessage());
        assertTrue(admin.isEnable());
        verify(userRepository, never()).save(any());
        verify(refreshTokenRepository, never()).revokeAllByUserId(any());
    }

    @Test
    void updateUser_disablingAdministrator_forbidden() {
        UUID userId = UUID.randomUUID();
        UserEntity admin = enabledUser(userId, role("ADMINISTRATOR"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));

        UpdateUserRequest request = UpdateUserRequest.builder().active(false).build();

        ProtectedRoleException ex = assertThrows(ProtectedRoleException.class,
                () -> userService.updateUser(userId, request));

        assertEquals(DISABLE_ADMIN_MESSAGE, ex.getMessage());
        assertTrue(admin.isEnable());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_strippingAdministrator_forbidden() {
        UUID userId = UUID.randomUUID();
        UserEntity admin = enabledUser(userId, role("ADMINISTRATOR"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role("USER")));

        UpdateUserRequest request = UpdateUserRequest.builder().roles(Set.of("USER")).build();

        ProtectedRoleException ex = assertThrows(ProtectedRoleException.class,
                () -> userService.updateUser(userId, request));

        assertEquals(STRIP_ADMIN_MESSAGE, ex.getMessage());
        assertEquals(Set.of("ADMINISTRATOR"), roleNames(admin));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_administratorWithEmptyRoles_forbidden() {
        UUID userId = UUID.randomUUID();
        UserEntity admin = enabledUser(userId, role("ADMINISTRATOR"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role("USER")));

        UpdateUserRequest request = UpdateUserRequest.builder().roles(Set.of()).build();

        ProtectedRoleException ex = assertThrows(ProtectedRoleException.class,
                () -> userService.updateUser(userId, request));

        assertEquals(STRIP_ADMIN_MESSAGE, ex.getMessage());
        assertEquals(Set.of("ADMINISTRATOR"), roleNames(admin));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_administratorKeepsRole_ok() {
        UUID userId = UUID.randomUUID();
        UserEntity admin = enabledUser(userId, role("ADMINISTRATOR"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));
        when(roleRepository.findByName("ADMINISTRATOR")).thenReturn(Optional.of(role("ADMINISTRATOR")));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role("USER")));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Nuevo")
                .roles(Set.of("ADMINISTRATOR", "USER"))
                .build();

        UserResponse response = userService.updateUser(userId, request);

        assertEquals("Nuevo", response.getFirstName());
        assertEquals(Set.of("ADMINISTRATOR", "USER"), response.getRoles());
        verify(userRepository).save(admin);
    }

    @Test
    void updateUser_twoAdministrators_stillForbidden() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        UserEntity first = enabledUser(firstId, role("ADMINISTRATOR"));
        UserEntity second = enabledUser(secondId, role("ADMINISTRATOR"));
        when(userRepository.findById(firstId)).thenReturn(Optional.of(first));
        when(userRepository.findById(secondId)).thenReturn(Optional.of(second));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role("USER")));

        UpdateUserRequest strip = UpdateUserRequest.builder().roles(Set.of("USER")).build();
        UpdateUserRequest disable = UpdateUserRequest.builder().active(false).build();

        assertThrows(ProtectedRoleException.class, () -> userService.updateUser(firstId, strip));
        assertThrows(ProtectedRoleException.class, () -> userService.updateUser(secondId, disable));
        assertThrows(ProtectedRoleException.class, () -> userService.deleteUser(firstId));

        assertTrue(first.isEnable());
        assertTrue(second.isEnable());
        assertEquals(Set.of("ADMINISTRATOR"), roleNames(first));
        verify(userRepository, never()).save(any());
    }

    // ─── Validación de roles ─────────────────────────

    @Test
    void updateUser_unknownRole_badRequestWithoutPartialChanges() {
        UUID userId = UUID.randomUUID();
        UserEntity user = enabledUser(userId, role("USER"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role("USER")));
        when(roleRepository.findByName("FANTASMA")).thenReturn(Optional.empty());

        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Otro")
                .roles(new java.util.LinkedHashSet<>(java.util.List.of("USER", "FANTASMA")))
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(userId, request));

        assertEquals("Unknown role: FANTASMA", ex.getMessage());
        assertEquals("Ana", user.getFirstName());
        assertEquals(Set.of("USER"), roleNames(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_unknownRole_badRequest() {
        when(roleRepository.findByName("FANTASMA")).thenReturn(Optional.empty());

        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Ana")
                .lastName("Perez")
                .email("ana@uis.edu.co")
                .password("Secreta123")
                .roles(Set.of("FANTASMA"))
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Unknown role: FANTASMA", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ─── Revocación de sesiones ──────────────────────

    @Test
    void deleteUser_regularUser_disablesAndRevokesTokens() {
        UUID userId = UUID.randomUUID();
        UserEntity user = enabledUser(userId, role("USER"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        assertFalse(user.isEnable());
        verify(userRepository).save(user);
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    void updateUser_disablingRegularUser_revokesTokens() {
        UUID userId = UUID.randomUUID();
        UserEntity user = enabledUser(userId, role("USER"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updateUser(userId, UpdateUserRequest.builder().active(false).build());

        assertFalse(user.isEnable());
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    void updateUser_enablingUser_doesNotRevoke() {
        UUID enablingId = UUID.randomUUID();
        UUID alreadyDisabledId = UUID.randomUUID();
        UserEntity disabled = enabledUser(enablingId, role("USER"));
        disabled.setEnable(false);
        UserEntity alreadyDisabled = enabledUser(alreadyDisabledId, role("USER"));
        alreadyDisabled.setEnable(false);
        when(userRepository.findById(enablingId)).thenReturn(Optional.of(disabled));
        when(userRepository.findById(alreadyDisabledId)).thenReturn(Optional.of(alreadyDisabled));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updateUser(enablingId, UpdateUserRequest.builder().active(true).build());
        userService.updateUser(alreadyDisabledId, UpdateUserRequest.builder().active(false).build());

        assertTrue(disabled.isEnable());
        verify(refreshTokenRepository, never()).revokeAllByUserId(any());
    }

    @Test
    void refresh_disabledUser_throwsInvalidToken() {
        UUID userId = UUID.randomUUID();
        UserEntity user = enabledUser(userId, role("USER"));
        user.setEnable(false);
        String token = "refresh-token";
        String jti = "jti-1";
        when(jwtUtil.extractJti(token)).thenReturn(jti);
        when(jwtUtil.extractTokenType(token)).thenReturn(JwtUtil.TOKEN_TYPE_REFRESH);
        when(jwtUtil.extractUserName(token)).thenReturn(user.getEmail());
        when(jwtUtil.extractExpirationAsLocalDateTime(token)).thenReturn(LocalDateTime.now().plusDays(1));
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.of(RefreshTokenEntity.builder()
                .jti(jti)
                .user(user)
                .revoked(false)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build()));
        when(userRepository.findUserEntityByEmailOrUsername(user.getEmail(), user.getEmail()))
                .thenReturn(Optional.of(user));

        InvalidSessionTokenException ex = assertThrows(InvalidSessionTokenException.class,
                () -> userService.refresh(token));

        assertEquals("La cuenta está deshabilitada", ex.getMessage());
        verify(refreshTokenRepository).revokeByJti(jti);
        verify(jwtUtil, never()).generateToken(any(), any(), any(), any(), anyBoolean());
    }

    @Test
    void changePassword_revokesRefreshTokens() {
        UUID userId = UUID.randomUUID();
        UserEntity user = enabledUser(userId, role("USER"));
        when(userRepository.findUserEntityByEmailOrUsername(user.getEmail(), user.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Actual12345", "hash")).thenReturn(true);
        when(passwordEncoder.encode("Nueva12345")).thenReturn("nuevo-hash");

        userService.changePassword(user.getEmail(), "Actual12345", "Nueva12345");

        assertEquals("nuevo-hash", user.getPassword());
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    void resetPassword_revokesRefreshTokens() {
        UUID userId = UUID.randomUUID();
        UserEntity user = enabledUser(userId, role("USER"));
        PasswordResetTokenEntity resetToken = PasswordResetTokenEntity.builder()
                .user(user)
                .token("reset-token")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        when(passwordResetTokenRepository.findByToken("reset-token")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(anyString())).thenReturn("nuevo-hash");

        userService.resetPassword("reset-token", "Nueva12345");

        assertEquals("nuevo-hash", user.getPassword());
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    // ─── Helpers ─────────────────────────────────────

    private static RoleEntity role(String name) {
        return RoleEntity.builder().guid(UUID.randomUUID()).name(name).isActive(true).build();
    }

    private static UserEntity enabledUser(UUID id, RoleEntity... roles) {
        return UserEntity.builder()
                .userId(id)
                .firstName("Ana")
                .lastName("Perez")
                .username("aperez")
                .email("ana@uis.edu.co")
                .password("hash")
                .isEnable(true)
                .roles(new HashSet<>(Set.of(roles)))
                .build();
    }

    private static Set<String> roleNames(UserEntity user) {
        Set<String> names = new HashSet<>();
        user.getRoles().forEach(r -> names.add(r.getName()));
        assertFalse(names.isEmpty());
        return names;
    }
}
