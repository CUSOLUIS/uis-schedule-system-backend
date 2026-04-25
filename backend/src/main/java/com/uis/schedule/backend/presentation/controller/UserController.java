package com.uis.schedule.backend.presentation.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.interfaces.UserService;

import io.swagger.v3.oas.annotations.Operation;
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

  @Operation(summary = "List users with pagination.", description = "Retrieves a paginated list of users. Requires authentication. Optionally filter by enabled and role.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the paginated list of users"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User not authenticated"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  public ResponseEntity<ApiResponse<PaginatedResponse<UserListDTO>>> listAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) Boolean enabled,
      @RequestParam(required = false) String role) {
    PaginatedResponse<UserListDTO> users = userService.listUsers(page, size, enabled, role);
    return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
  }

  @Operation(summary = "Get users by status", description = "Retrieves a paginated list of users filtered by their enable status. Requires ADMINISTRADOR role.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the paginated list of users by status"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required ADMINISTRADOR role"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/status/{status}")
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  public ResponseEntity<ApiResponse<PaginatedResponse<UserListDTO>>> getByStatus(
      @PathVariable boolean status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PaginatedResponse<UserListDTO> users = userService.findByStatus(status, page, size);
    return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully by status"));
  }

  @Operation(summary = "Get users by role", description = "Retrieves a paginated list of users filtered by their role name. Requires ADMINISTRADOR role.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the paginated list of users by role"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required ADMINISTRADOR role"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/role/{roleName}")
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  public ResponseEntity<ApiResponse<PaginatedResponse<UserListDTO>>> getByRole(
      @PathVariable String roleName,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PaginatedResponse<UserListDTO> users = userService.findByRole(roleName, page, size);
    return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully by role"));
  }

  @Operation(summary = "Get user by ID", description = "Retrieves detailed information of a single user by their ID.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found with the specified ID"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<UserDetailDTO>> getUserById(@PathVariable UUID id) {
    UserDetailDTO user = userService.findUserById(id)
        .orElseThrow(() -> new com.uis.schedule.backend.service.exception.UserNotFoundException(
            "User not found with ID: " + id));
    return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
  }

  @Operation(summary = "Create a new user", description = "Creates a new user in the system. Validates input data.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid user data or email already in use"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
    UserResponse createdUser = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(createdUser, "User created successfully"));
  }

  @Operation(summary = "Update an existing user", description = "Updates an existing user's details by their ID. Only provided fields will be updated.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid user data or email already in use"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found with the specified ID"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateUserRequest request) {
    UserResponse updatedUser = userService.updateUser(id, request);
    return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
  }

  @Operation(summary = "Delete a user by ID", description = "Deletes a user from the system by their ID.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found with the specified ID"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully (soft delete)"));
  }
}
