package com.uis.schedule.backend.service.interfaces;

import java.util.Optional;
import java.util.UUID;

import com.uis.schedule.backend.presentation.dto.AuthResponse;
import com.uis.schedule.backend.presentation.dto.AuthSignupRequest;
import com.uis.schedule.backend.presentation.dto.CreateUserRequest;
import com.uis.schedule.backend.presentation.dto.PaginatedResponse;
import com.uis.schedule.backend.presentation.dto.UpdateUserRequest;
import com.uis.schedule.backend.presentation.dto.UserDetailDTO;
import com.uis.schedule.backend.presentation.dto.UserListDTO;
import com.uis.schedule.backend.presentation.dto.UserResponse;

/**
 * Service interface for user management operations.
 * Provides CRUD operations and authentication methods.
 */
public interface UserService {

  /**
   * Retrieves a list of users inactive or active with minimal information and
   * pagination.
   *
   * @param page   page number (0-indexed)
   * @param size   number of items per page
   * @param active filter by active or inactive users
   * @return PaginatedResponse of UserListDTO
   */
  PaginatedResponse<UserListDTO> listAllUsersByActive(int page, int size, boolean active);

  /**
   * Retrieves a list of users with minimal information and pagination.
   *
   * @param page page number (0-indexed)
   * @param size number of items per page
   * @param enabled filter if user is enabled or not
   * @param role filter by role
   * @return PaginatedResponse of UserListDTO
   */
  PaginatedResponse<UserListDTO> listUsers(int page, int size, Boolean enabled, String role);

  /**
   * Finds users by their enable status with pagination.
   *
   * @param status the status to filter by
   * @param page   page number (0-indexed)
   * @param size   number of items per page
   * @return PaginatedResponse of UserListDTO
   */
  PaginatedResponse<UserListDTO> findByStatus(boolean status, int page, int size);

  /**
   * Finds users by their role name with pagination.
   *
   * @param roleName the role name to filter by
   * @param page     page number (0-indexed)
   * @param size     number of items per page
   * @return PaginatedResponse of UserListDTO
   */
  PaginatedResponse<UserListDTO> findByRole(String roleName, int page, int size);

  /**
   * Finds a user by their ID with complete details.
   *
   * @param id the user ID
   * @return Optional containing UserDetailDTO if found
   */
  Optional<UserDetailDTO> findUserById(UUID id);

  /**
   * Creates a new user.
   *
   * @param request the create user request
   * @return UserResponse with created user data
   */
  UserResponse createUser(CreateUserRequest request);

  /**
   * Updates an existing user.
   *
   * @param id      the user ID
   * @param request the update user request
   * @return UserResponse with updated user data
   */
  UserResponse updateUser(UUID id, UpdateUserRequest request);

  /**
   * Deletes a user by ID.
   *
   * @param id the user ID
   */
  void deleteUser(UUID id);

  // Authentication methods (kept for backward compatibility)

  /**
   * Registers a new user.
   *
   * @param authSignupRequest the signup request
   * @return AuthResponse with registration result
   */
  AuthResponse signUp(AuthSignupRequest authSignupRequest);

  /**
   * Authenticates a user.
   *
   * @param email    the user email
   * @param password the user password
   * @return AuthResponse with authentication result
   */
  AuthResponse login(String email, String password);

  /**
   * Solicita la recuperación de contraseña para un correo.
   *
   * @param email correo del usuario
   */
  void requestPasswordReset(String email);

  /**
   * Restablece la contraseña usando un token válido.
   *
   * @param token       token de recuperación
   * @param newPassword nueva contraseña
   */
  void resetPassword(String token, String newPassword);

  /**
   * Cambia la contraseña de un usuario autenticado.
   *
   * @param usernameOrEmail identificador del usuario autenticado
   * @param currentPassword contraseña actual
   * @param newPassword     nueva contraseña
   */
  void changePassword(String usernameOrEmail, String currentPassword, String newPassword);

  /**
   * Cierra la sesión de un usuario autenticado invalidando su token en el
   * servidor. Una vez invalidado, cualquier solicitud posterior que use ese
   * mismo token debe ser rechazada.
   *
   * @param accessToken  el access token (sin el prefijo "Bearer ") a invalidar
   * @param refreshToken el refresh token asociado a revocar (opcional, puede ser null)
   */
  void logout(String accessToken, String refreshToken);

  /**
   * Emite un nuevo access token (y rota el refresh token) a partir de un
   * refresh token válido, no revocado y no expirado.
   *
   * @param refreshToken el refresh token entregado por el cliente
   * @return AuthResponse con el nuevo access token y el nuevo refresh token
   */
  AuthResponse refresh(String refreshToken);
}
