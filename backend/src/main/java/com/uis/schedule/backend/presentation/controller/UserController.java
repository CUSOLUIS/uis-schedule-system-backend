package com.uis.schedule.backend.presentation.controller;

import java.util.List;

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

	@Operation(summary = "List all users", description = "Retrieves a list of all registered users with minimal information. Requires ADMIN role. Returns empty list if no users exist.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the list of users (may be empty)"),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required ADMIN role")
	})
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<UserListDTO>> listAll() {
		List<UserListDTO> users = userService.listUsers();
		return ResponseEntity.ok(users);
	}

	@Operation(summary = "Get user by ID", description = "Retrieves detailed information of a single user by their ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
			@ApiResponse(responseCode = "404", description = "User not found with the specified ID")
	})
	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UserDetailDTO> getUserById(@PathVariable Long id) {
		return userService.findUserById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Create a new user", description = "Creates a new user in the system. Validates input data.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid user data or email already in use"),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions")
	})
	@PostMapping
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
		try {
			UserResponse createdUser = userService.createUser(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@Operation(summary = "Update an existing user", description = "Updates an existing user's details by their ID. Only provided fields will be updated.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid user data or email already in use"),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
			@ApiResponse(responseCode = "404", description = "User not found with the specified ID")
	})
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<UserResponse> updateUser(
			@PathVariable Long id,
			@Valid @RequestBody UpdateUserRequest request) {
		try {
			UserResponse updatedUser = userService.updateUser(id, request);
			return ResponseEntity.ok(updatedUser);
		} catch (UserNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@Operation(summary = "Delete a user by ID", description = "Deletes a user from the system by their ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "User deleted successfully"),
			@ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
			@ApiResponse(responseCode = "404", description = "User not found with the specified ID")
	})
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		try {
			userService.deleteUser(id);
			return ResponseEntity.noContent().build();
		} catch (UserNotFoundException e) {
			System.err.println(e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}
