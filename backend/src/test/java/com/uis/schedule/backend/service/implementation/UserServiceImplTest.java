package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.configuration.jwt.JwtUtil;
import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.PasswordResetTokenRepository;
import com.uis.schedule.backend.persistence.repository.RefreshTokenRepository;
import com.uis.schedule.backend.persistence.repository.RevokedTokenRepository;
import com.uis.schedule.backend.persistence.repository.RoleRepository;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.CreateUserRequest;
import com.uis.schedule.backend.presentation.dto.UpdateUserRequest;
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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

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
    @Mock
    private TeacherProfileService teacherProfileService;

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
                refreshTokenRepository,
                teacherProfileService);
    }

    @Test
    void deleteUser_administrator_throwsProtectedRoleException() {
        UUID userId = UUID.randomUUID();
        UserEntity admin = enabledUser(userId, role("ADMINISTRATOR"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));

        ProtectedRoleException ex = assertThrows(ProtectedRoleException.class, () -> userService.deleteUser(userId));

        assertEquals("Security protection: Users with ADMINISTRATOR role cannot be deleted.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_disablingAdministrator_throwsProtectedRoleException() {
        UUID userId = UUID.randomUUID();
        UserEntity admin = enabledUser(userId, role("ADMINISTRATOR"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));

        UpdateUserRequest request = UpdateUserRequest.builder().active(false).build();

        ProtectedRoleException ex = assertThrows(ProtectedRoleException.class,
                () -> userService.updateUser(userId, request));

        assertEquals("Security protection: Users with ADMINISTRATOR role cannot be disabled.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_unknownRole_throwsBadRequest() {
        UUID userId = UUID.randomUUID();
        UserEntity user = enabledUser(userId, role("USER"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("NOT_A_ROLE")).thenReturn(Optional.empty());

        UpdateUserRequest request = UpdateUserRequest.builder().roles(Set.of("NOT_A_ROLE")).build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(userId, request));

        assertEquals("Unknown role: NOT_A_ROLE", ex.getMessage());
    }

    @Test
    void updateUser_strippingLastAdministrator_throwsProtectedRoleException() {
        UUID userId = UUID.randomUUID();
        UserEntity admin = enabledUser(userId, role("ADMINISTRATOR"));
        RoleEntity userRole = role("USER");
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.countByRoles_NameAndIsEnableTrueAndUserIdNot("ADMINISTRATOR", userId)).thenReturn(0L);

        UpdateUserRequest request = UpdateUserRequest.builder().roles(Set.of("USER")).build();

        ProtectedRoleException ex = assertThrows(ProtectedRoleException.class,
                () -> userService.updateUser(userId, request));

        assertEquals("Security protection: The system must keep at least one active administrator.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_withTeacherRole_ensuresTeacherProfile() {
        RoleEntity teacherRole = role("TEACHER");
        when(roleRepository.findByName("TEACHER")).thenReturn(Optional.of(teacherRole));
        when(passwordEncoder.encode("password1")).thenReturn("hash");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity saved = invocation.getArgument(0);
            saved.setUserId(UUID.randomUUID());
            return saved;
        });

        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Ana")
                .lastName("Ruiz")
                .email("ana@uis.edu")
                .password("password1")
                .roles(Set.of("TEACHER"))
                .build();

        userService.createUser(request);

        verify(teacherProfileService).ensureFor(any(UserEntity.class));
    }

    private UserEntity enabledUser(UUID id, RoleEntity role) {
        return UserEntity.builder()
                .userId(id)
                .firstName("Admin")
                .lastName("User")
                .username("admin")
                .email("admin@uis.edu")
                .password("hash")
                .isEnable(true)
                .roles(Set.of(role))
                .build();
    }

    private RoleEntity role(String name) {
        return RoleEntity.builder().name(name).isActive(true).build();
    }
}
