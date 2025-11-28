package com.uis.schedule.backend.controller;

import com.uis.schedule.backend.dto.ApiResponse;
import com.uis.schedule.backend.dto.UserDTO;
import com.uis.schedule.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:80"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Error retrieving users: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        try {
            UserDTO user = userService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(ApiResponse.success(user, "User found"));
            } else {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Error retrieving user: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity.ok(ApiResponse.success(createdUser, "User created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("Error creating user: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable String id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            if (updatedUser != null) {
                return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
            } else {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("Error updating user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
            } else {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Error deleting user: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDTO>>> searchUsers(@RequestParam String q) {
        try {
            List<UserDTO> users = userService.searchUsers(q);
            return ResponseEntity.ok(ApiResponse.success(users, "Search completed"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Error searching users: " + e.getMessage()));
        }
    }
}