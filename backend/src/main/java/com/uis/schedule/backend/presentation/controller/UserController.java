package com.uis.schedule.backend.presentation.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.service.interfaces.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for user management operations.
 * Provides endpoints for CRUD operations on users.
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "List active users with pagination", description = "Retrieves a paginated list of active users. Requires authentication.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the paginated list of active users"),
			@ApiResponse(responseCode = "403", description = "Forbidden - User not authenticated"),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class)))
	})
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> listAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			PaginatedResponse<UserListDTO> users = userService.listUsers(page, size);
			return ResponseEntity.ok(users);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(null, "Error retrieving users: " + e.getMessage()));
		}
	}

	@Operation(summary = "List all users including inactive with pagination", description = "Retrieves a paginated list of all users, including those that are inactive. Requires ADMINISTRADOR role.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the paginated list of all users"),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required ADMINISTRADOR role"),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class)))
	})
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<?> listAllWithInactive(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			PaginatedResponse<UserListDTO> users = userService.listAllUsersIncludingInactive(page, size);
			return ResponseEntity.ok(users);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(null, "Error retrieving all users: " + e.getMessage()));
		}
	}

	@Operation(summary = "Get users by status", description = "Retrieves a paginated list of users filtered by their enable status. Requires ADMINISTRADOR role.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the paginated list of users by status"),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required ADMINISTRADOR role"),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class)))
	})
	@GetMapping("/status/{status}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<?> getByStatus(
			@PathVariable boolean status,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			PaginatedResponse<UserListDTO> users = userService.findByStatus(status, page, size);
			return ResponseEntity.ok(users);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(null, "Error retrieving users by status: " + e.getMessage()));
		}
	}

	@Operation(summary = "Get users by role", description = "Retrieves a paginated list of users filtered by their role name. Requires ADMINISTRADOR role.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the paginated list of users by role"),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required ADMINISTRADOR role"),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class)))
	})
	@GetMapping("/role/{roleName}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<?> getByRole(
			@PathVariable String roleName,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			PaginatedResponse<UserListDTO> users = userService.findByRole(roleName, page, size);
			return ResponseEntity.ok(users);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(null, "Error retrieving users by role: " + e.getMessage()));
		}
	}

	@Operation(summary = "Get user by ID", description = "Retrieves detailed information of a single user by their ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
			@ApiResponse(responseCode = "404", description = "User not found with the specified ID", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class)))
	})
	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> getUserById(@PathVariable UUID id) {
		try {
			java.util.Optional<UserDetailDTO> user = userService.findUserById(id);
			if (user.isPresent()) {
				return ResponseEntity.ok(user.get());
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new AuthResponse(null, "User not found with ID: " + id));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(null, "Error retrieving user: " + e.getMessage()));
		}
	}

	@Operation(summary = "Create a new user", description = "Creates a new user in the system. Validates input data.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid user data or email already in use", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class)))
	})
	@PostMapping
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
		try {
			UserResponse createdUser = userService.createUser(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new AuthResponse(null, e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(null, "User creation failed: " + e.getMessage()));
		}
	}

	@Operation(summary = "Update an existing user", description = "Updates an existing user's details by their ID. Only provided fields will be updated.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid user data or email already in use", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
			@ApiResponse(responseCode = "404", description = "User not found with the specified ID", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class)))
	})
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<?> updateUser(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateUserRequest request) {
		try {
			UserResponse updatedUser = userService.updateUser(id, request);
			return ResponseEntity.ok(updatedUser);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AuthResponse(null, e.getMessage()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new AuthResponse(null, e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(null, "Update failed: " + e.getMessage()));
		}
	}

	@Operation(summary = "Delete a user by ID", description = "Deletes a user from the system by their ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User deleted successfully", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
			@ApiResponse(responseCode = "404", description = "User not found with the specified ID", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthResponse.class)))
	})
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
		try {
			userService.deleteUser(id);
			return ResponseEntity.ok(new AuthResponse(null, "User deleted successfully (soft delete)"));
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AuthResponse(null, e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(null, "Deletion failed: " + e.getMessage()));
		}
	}
}
