package com.uis.schedule.backend.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.uis.schedule.backend.presentation.dto.*;

/**
 * Service interface for user management operations.
 * Provides CRUD operations and authentication methods.
 */
public interface UserService {

	/**
	 * Retrieves a list of all users with minimal information.
	 * 
	 * @return List of UserListDTO, empty list if no users exist
	 */
	List<UserListDTO> listUsers();

	/**
	 * Finds a user by their ID with complete details.
	 * 
	 * @param id the user ID
	 * @return Optional containing UserDetailDTO if found
	 */
	Optional<UserDetailDTO> findUserById(Long id);

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
	UserResponse updateUser(Long id, UpdateUserRequest request);

	/**
	 * Deletes a user by ID.
	 * 
	 * @param id the user ID
	 */
	void deleteUser(Long id);

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
}
