package com.uis.schedule.backend.presentation.controller;


import java.util.List;
import java.util.Optional;

//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.presentation.dto.UserDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService){
		this.userService = userService;
	}

	@Operation(summary = "List all users", description = "Retrieves a list of all registered users. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of users"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required ADMIN role")
    })
	@GetMapping("")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public List<UserDTO> listAll() {
		return userService.listUsers();
	}

	@Operation(summary = "Get user by ID", description = "Retrieves a single user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
            @ApiResponse(responseCode = "404", description = "User not found with the specified ID")
    })
	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
		return userService.findUserById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Create a new user", description = "Creates a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data or email already in use"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions")
    })
	@PostMapping
	public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user){
		return new ResponseEntity<>(userService.createUser(user),HttpStatus.OK);
	}

	@Operation(summary = "Update an existing user", description = "Updates an existing user's details by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data or email already in use"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
            @ApiResponse(responseCode = "404", description = "User not found with the specified ID")
    })
	@PutMapping("/{id}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO user){
		try {
            UserDTO updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
	}

	@Operation(summary = "Delete a user by ID", description = "Deletes a user from the system by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required permissions"),
            @ApiResponse(responseCode = "404", description = "User not found with the specified ID")
    })
	@DeleteMapping("/{id}")
	public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id){
		return userService.findUserById(id)
			.map(c -> {
				userService.deleteUser(id);
				return ResponseEntity.ok(c);
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
