package com.uis.schedule.backend.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uis.schedule.backend.service.exception.UserAlreadyExistsException;
import com.uis.schedule.backend.configuration.jwt.JwtUtil;
import com.uis.schedule.backend.persistence.entity.PasswordResetTokenEntity;
import com.uis.schedule.backend.persistence.entity.RefreshTokenEntity;
import com.uis.schedule.backend.persistence.entity.RevokedTokenEntity;
import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.PasswordResetTokenRepository;
import com.uis.schedule.backend.persistence.repository.RefreshTokenRepository;
import com.uis.schedule.backend.persistence.repository.RoleRepository;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.AuthResponse;
import com.uis.schedule.backend.presentation.dto.AuthSignupRequest;
import com.uis.schedule.backend.presentation.dto.CreateUserRequest;
import com.uis.schedule.backend.presentation.dto.PaginatedResponse;
import com.uis.schedule.backend.presentation.dto.UpdateUserRequest;
import com.uis.schedule.backend.presentation.dto.UserDetailDTO;
import com.uis.schedule.backend.presentation.dto.UserListDTO;
import com.uis.schedule.backend.presentation.dto.UserResponse;
import com.uis.schedule.backend.service.exception.InvalidTokenException;
import com.uis.schedule.backend.persistence.repository.RevokedTokenRepository;
import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.util.TokenGenerator;
import com.uis.schedule.backend.util.mapper.UserMapper;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of UserService interface.
 * Handles all user-related business logic following best practices.
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final TokenGenerator tokenGenerator;
  private final EmailService emailService;
  private final UserService self;
  private final RevokedTokenRepository revokedTokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  @org.springframework.beans.factory.annotation.Value("${app.password-reset.expiration-hours:2}")
  private int passwordResetExpirationHours;

  @Autowired
  public UserServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      AuthenticationManager authenticationManager,
      JwtUtil jwtUtil,
      PasswordEncoder passwordEncoder,
      PasswordResetTokenRepository passwordResetTokenRepository,
      TokenGenerator tokenGenerator,
      EmailService emailService,
      @Lazy UserService self,
      RevokedTokenRepository revokedTokenRepository,
      RefreshTokenRepository refreshTokenRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = passwordEncoder;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
    this.tokenGenerator = tokenGenerator;
    this.emailService = emailService;
    this.self = self;
    this.revokedTokenRepository = revokedTokenRepository;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedResponse<UserListDTO> listUsers(int page, int size, Boolean isEnabled, String role) {
    try {
      org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
          size);

      Specification<UserEntity> spec = Specification.where(null);

      if (isEnabled != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("isEnabled"), isEnabled));
      }

      if (role != null && !role.isBlank()) {
        spec = spec.and((root, query, cb) -> {
          Join<UserEntity, RoleEntity> roles = root.join("roles", JoinType.INNER);
          return cb.equal(roles.get("name"), role);
        });
      }

      org.springframework.data.domain.Page<UserEntity> usersPage = userRepository.findAll(spec, pageable);

      return convertToPaginatedResponse(usersPage);
    } catch (Exception e) {
      log.error("Error retrieving all users list", e);
      return new PaginatedResponse<>();
    }
  }

  private PaginatedResponse<UserListDTO> convertToPaginatedResponse(
      org.springframework.data.domain.Page<UserEntity> usersPage) {
    List<UserListDTO> content = usersPage.getContent().stream()
        .map(UserMapper::entityToListDTO)
        .collect(Collectors.toList());

    return PaginatedResponse.<UserListDTO>builder()
        .content(content)
        .pageNumber(usersPage.getNumber())
        .pageSize(usersPage.getSize())
        .totalElements(usersPage.getTotalElements())
        .totalPages(usersPage.getTotalPages())
        .last(usersPage.isLast())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public java.util.Optional<UserDetailDTO> findUserById(UUID id) {
    if (id == null) {
      log.warn("Attempted to find user with null ID");
      return java.util.Optional.empty();
    }

    return userRepository.findByUserIdAndIsEnableTrue(id)
        .map(UserMapper::entityToDetailDTO);
  }

  @Override
  public UserResponse createUser(CreateUserRequest request) {
    int min = 1;
    int max = 1000;
    // Formula: (int) (Math.random() * (max - min + 1) + min)
    int randomInt = (int) (Math.random() * (max - min + 1) + min);

    if (request == null) {
      throw new IllegalArgumentException("Create user request cannot be null");
    }

    log.info("Creating new user with email: {}", request.getEmail());

    try {
      String email = request.getEmail().toLowerCase();
      String username = (request.getFirstName().substring(0, 1) + request.getLastName() + randomInt)
          .toLowerCase();

      java.util.Set<RoleEntity> roles = new java.util.HashSet<>();
      if (request.getRoles() != null && !request.getRoles().isEmpty()) {
        request.getRoles().forEach(roleName -> {
          roleRepository.findByName(roleName).ifPresent(roles::add);
        });
      } else {
        roleRepository.findByName("USER").ifPresent(roles::add);
      }

      UserEntity entity = UserEntity.builder()
          .firstName(request.getFirstName())
          .lastName(request.getLastName())
          .username(username)
          .email(email)
          .password(passwordEncoder.encode(request.getPassword()))
          .roles(roles)
          .isEnable(true)
          .accountNoExpired(true)
          .accountNoLocked(true)
          .credentialNoExpired(true)
          .build();
      UserEntity savedEntity = userRepository.save(entity);

      log.info("User created successfully with ID: {}", savedEntity.getUserId());
      return UserMapper.entityToResponse(savedEntity);

    } catch (DataIntegrityViolationException e) {
      log.error("Data integrity violation while creating user: {}", request.getEmail(), e);
      throw new IllegalArgumentException(
          "User creation failed: The email '" + request.getEmail() + "' may already be in use.");
    } catch (Exception e) {
      log.error("Unexpected error while creating user: {}", request.getEmail(), e);
      throw new RuntimeException("Failed to create user", e);
    }
  }

  @Override
  public UserResponse updateUser(UUID id, UpdateUserRequest request) {
    if (id == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    if (request == null) {
      throw new IllegalArgumentException("Update user request cannot be null");
    }

    log.info("Updating user with ID: {}", id);

    UserEntity userToUpdate = userRepository.findById(id)
        .orElseThrow(() -> {
          log.error("User not found with ID: {}", id);
          return new UserNotFoundException("User not found with id: " + id);
        });

    if (request.getEmail() != null) {
      request.setEmail(request.getEmail().toLowerCase());
    }

    try {
      UserMapper.updateEntityFromRequest(userToUpdate, request);

      if (request.getRoles() != null) {
        java.util.Set<RoleEntity> roles = new java.util.HashSet<>();
        request.getRoles().forEach(roleName -> {
          roleRepository.findByName(roleName).ifPresent(roles::add);
        });
        userToUpdate.setRoles(roles);
      }

      UserEntity updatedUser = userRepository.save(userToUpdate);

      log.info("User updated successfully with ID: {}", id);
      return UserMapper.entityToResponse(updatedUser);

    } catch (DataIntegrityViolationException e) {
      log.error("Data integrity violation while updating user: {}", id, e);
      throw new IllegalArgumentException(
          "Email '" + request.getEmail() + "' is already in use by another user.");
    } catch (Exception e) {
      log.error("Unexpected error while updating user: {}", id, e);
      throw new RuntimeException("Failed to update user", e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedResponse<UserListDTO> findByStatus(boolean status, int page, int size) {
    try {
      org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
          size);
      org.springframework.data.domain.Page<UserEntity> usersPage = userRepository.findAllByIsEnable(status,
          pageable);
      return convertToPaginatedResponse(usersPage);
    } catch (Exception e) {
      log.error("Error retrieving users by status: {}", status, e);
      return new PaginatedResponse<>();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedResponse<UserListDTO> listAllUsersByActive(int page, int size, boolean active) {
    return self.findByStatus(active, page, size);
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedResponse<UserListDTO> findByRole(String roleName, int page, int size) {
    try {
      org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
          size);
      org.springframework.data.domain.Page<UserEntity> usersPage = userRepository.findByRolesName(roleName,
          pageable);
      return convertToPaginatedResponse(usersPage);
    } catch (Exception e) {
      log.error("Error retrieving users by role: {}", roleName, e);
      return new PaginatedResponse<>();
    }
  }

  @Override
  public void deleteUser(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }

    log.info("Soft deleting user with ID: {}", id);

    // We use findById here to see if user exists, regardless of current enable status
    UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.error("User not found with ID: {}", id);
          return new UserNotFoundException("User not found with id: " + id);
        });

    if (!user.isEnable()) {
      throw new IllegalArgumentException("User is already disabled (soft-deleted)");
    }

    // Protection: An admin cannot delete another admin
    boolean isTargetAdmin = user.getRoles().stream()
        .anyMatch(role -> role.getName().equals("ADMINISTRATOR"));

    if (isTargetAdmin) {
      log.warn("Attempt to delete an ADMINISTRATOR account blocked for ID: {}", id);
      throw new IllegalStateException("Security protection: Users with ADMINISTRATOR role cannot be deleted.");
    }

    user.setEnable(false);
    userRepository.save(user);

    log.info("User soft-deleted successfully with ID: {}", id);
  }

  @Override
  public AuthResponse signUp(AuthSignupRequest authSignupRequest) {
    String email = authSignupRequest.email().toLowerCase();
    log.info("Registro interno de un usuario {}.", email);

    String username = (authSignupRequest.firstName().substring(0, 1) + authSignupRequest.lastName())
        .toLowerCase();

    UserEntity existingUser = userRepository
        .findUserEntityByEmailOrUsername(email, username)
        .orElse(null);

    if (existingUser != null) {
      log.warn("Email already registered or username taken: {}", authSignupRequest.email());
      throw new UserAlreadyExistsException("El correo o el nombre de usuario ya están en uso");
    }

    UserEntity newUser = new UserEntity();
    newUser.setFirstName(authSignupRequest.firstName());
    newUser.setLastName(authSignupRequest.lastName());
    newUser.setUsername(username);
    newUser.setEmail(email);
    newUser.setPassword(passwordEncoder.encode(authSignupRequest.password()));
    newUser.setEnable(true);
    newUser.setAccountNoExpired(true);
    newUser.setAccountNoLocked(true);
    newUser.setCredentialNoExpired(true);

    java.util.Set<RoleEntity> roles = new java.util.HashSet<>();
    roleRepository.findByName("USER").ifPresent(roles::add);
    newUser.setRoles(roles);

    userRepository.save(newUser);
    log.info("User registered successfully: {}", authSignupRequest.email());

    java.util.Set<String> rolesSet = newUser.getRoles().stream()
        .map(RoleEntity::getName)
        .collect(Collectors.toSet());

    String token = jwtUtil.generateToken(
        newUser.getUserId(),
        newUser.getUsername(),
        newUser.getEmail(),
        rolesSet,
        newUser.isEnable());
    String refreshToken = issueRefreshToken(newUser);

    return new AuthResponse(token, refreshToken, "User registered successfully");
  }

  @Override
  public AuthResponse login(String email, String password) {
    String normalizedEmail = email.toLowerCase();
    log.info("Login attempt for email: {}", normalizedEmail);

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(normalizedEmail, password));

    String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
        .getUsername();

    UserEntity user = userRepository.findUserEntityByEmailOrUsername(username, username)
        .orElseThrow(() -> {
          log.error("Authenticated user not found in database: {}", username);
          return new UserNotFoundException("Authenticated user not found in database: " + username);
        });

    java.util.Set<String> rolesSet = user.getRoles().stream()
        .map(RoleEntity::getName)
        .collect(Collectors.toSet());

    String token = jwtUtil.generateToken(
        user.getUserId(),
        user.getUsername(),
        user.getEmail(),
        rolesSet,
        user.isEnable());

    log.info("Login successful for user: {} with roles: {}", username, rolesSet);
    String refreshToken = issueRefreshToken(user);
    return new AuthResponse(token, refreshToken, "Login successful");
  }

  /**
   * Genera un refresh token para el usuario y lo persiste como registro
   * activo ("whitelist"), necesario para poder validarlo/rotarlo/revocarlo
   * en {@link #refresh(String)} y {@link #logout(String, String)}.
   */
  private String issueRefreshToken(UserEntity user) {
    String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getEmail());
    String jti = jwtUtil.extractJti(refreshToken);
    LocalDateTime expiresAt = jwtUtil.extractExpirationAsLocalDateTime(refreshToken);

    RefreshTokenEntity entity = RefreshTokenEntity.builder()
        .jti(jti)
        .user(user)
        .createdAt(LocalDateTime.now())
        .expiresAt(expiresAt)
        .revoked(false)
        .build();
    refreshTokenRepository.save(entity);

    return refreshToken;
  }

  @Override
  public void requestPasswordReset(String email) {
    String normalizedEmail = email.toLowerCase().trim();

    UserEntity user = userRepository.findUserEntityByEmailOrUsername(normalizedEmail, normalizedEmail)
        .orElse(null);

    // No exponer si el correo existe o no para evitar enumeración de cuentas.
    if (user == null) {
      log.info("Solicitud de recuperación para correo no registrado: {}", normalizedEmail);
      return;
    }

    passwordResetTokenRepository.deleteByUserUserId(user.getUserId());

    PasswordResetTokenEntity resetToken = PasswordResetTokenEntity.builder()
        .user(user)
        .token(tokenGenerator.generateToken())
        .createdAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusHours(passwordResetExpirationHours))
        .build();

    passwordResetTokenRepository.save(resetToken);
    emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
  }

  @Override
  public void resetPassword(String token, String newPassword) {
    if (newPassword == null || newPassword.length() < 8 || newPassword.length() > 256) {
      throw new IllegalArgumentException("La nueva contraseña debe tener entre 8 y 256 caracteres");
    }

    PasswordResetTokenEntity resetToken = passwordResetTokenRepository.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación no es válido"));

    if (resetToken.getUsedAt() != null) {
      throw new IllegalArgumentException("El enlace de recuperación ya fue utilizado");
    }

    if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("El enlace de recuperación ha expirado");
    }

    UserEntity user = resetToken.getUser();
    user.setPassword(passwordEncoder.encode(newPassword));
    resetToken.setUsedAt(LocalDateTime.now());

    userRepository.save(user);
    passwordResetTokenRepository.save(resetToken);
  }

  @Override
  public void changePassword(String usernameOrEmail, String currentPassword, String newPassword) {
    if (newPassword == null || newPassword.length() < 8 || newPassword.length() > 256) {
      throw new IllegalArgumentException("La nueva contraseña debe tener entre 8 y 256 caracteres");
    }

    if (newPassword.equals(currentPassword)) {
      throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
    }

    UserEntity user = userRepository.findUserEntityByEmailOrUsername(usernameOrEmail, usernameOrEmail)
        .orElseThrow(() -> new UserNotFoundException("Usuario autenticado no encontrado"));

    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
      throw new IllegalArgumentException("La contraseña actual no es correcta");
    }

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  @Override
  public void logout(String accessToken, String refreshToken) {
    if (accessToken == null || accessToken.isBlank()) {
      throw new InvalidTokenException("Token inválido");
    }

    String jti;
    LocalDateTime expiresAt;
    String username;
    try {
      jti = jwtUtil.extractJti(accessToken);
      expiresAt = jwtUtil.extractExpirationAsLocalDateTime(accessToken);
      username = jwtUtil.extractUserName(accessToken);
    } catch (Exception ex) {
      throw new InvalidTokenException("Token inválido o expirado");
    }

    if (jti == null || jti.isBlank()) {
      throw new InvalidTokenException("El token no contiene un identificador válido");
    }

    if (!revokedTokenRepository.existsByJti(jti)) {
      UserEntity user = userRepository.findUserEntityByEmailOrUsername(username, username)
          .orElseThrow(() -> new UserNotFoundException("Usuario autenticado no encontrado"));

      RevokedTokenEntity revokedToken = RevokedTokenEntity.builder()
          .jti(jti)
          .user(user)
          .revokedAt(LocalDateTime.now())
          .expiresAt(expiresAt)
          .build();

      revokedTokenRepository.save(revokedToken);
      log.info("Logout exitoso para el usuario {} (jti={})", username, jti);
    } else {
      log.info("Logout: el token con jti {} ya se encontraba revocado", jti);
    }

    // Revocar también el refresh token asociado, si el cliente lo envía,
    // para que no pueda usarse posteriormente para obtener nuevos access tokens.
    if (refreshToken != null && !refreshToken.isBlank()) {
      try {
        String refreshJti = jwtUtil.extractJti(refreshToken);
        if (refreshJti != null && !refreshJti.isBlank()) {
          refreshTokenRepository.revokeByJti(refreshJti);
        }
      } catch (Exception ex) {
        log.warn("No fue posible revocar el refresh token durante el logout: {}", ex.getMessage());
      }
    }
  }

  @Override
  public AuthResponse refresh(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new InvalidTokenException("Refresh token inválido");
    }

    String jti;
    String tokenType;
    String email;
    try {
      jti = jwtUtil.extractJti(refreshToken);
      tokenType = jwtUtil.extractTokenType(refreshToken);
      email = jwtUtil.extractUserName(refreshToken);
      jwtUtil.extractExpirationAsLocalDateTime(refreshToken);
    } catch (Exception ex) {
      throw new InvalidTokenException("Refresh token inválido o expirado");
    }

    if (!JwtUtil.TOKEN_TYPE_REFRESH.equals(tokenType)) {
      throw new InvalidTokenException("El token proporcionado no es un refresh token");
    }

    RefreshTokenEntity storedToken = refreshTokenRepository.findByJti(jti)
        .orElseThrow(() -> new InvalidTokenException("Refresh token no reconocido"));

    if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new InvalidTokenException("Refresh token revocado o expirado");
    }

    UserEntity user = userRepository.findUserEntityByEmailOrUsername(email, email)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

    // Rotación: se revoca el refresh token usado y se emite uno nuevo, para
    // limitar la ventana de uso de cada refresh token en caso de robo.
    refreshTokenRepository.revokeByJti(jti);

    java.util.Set<String> rolesSet = user.getRoles().stream()
        .map(RoleEntity::getName)
        .collect(Collectors.toSet());

    String newAccessToken = jwtUtil.generateToken(
        user.getUserId(),
        user.getUsername(),
        user.getEmail(),
        rolesSet,
        user.isEnable());
    String newRefreshToken = issueRefreshToken(user);

    log.info("Refresh exitoso para el usuario {}", email);
    return new AuthResponse(newAccessToken, newRefreshToken, "Token renovado correctamente");
  }
}
