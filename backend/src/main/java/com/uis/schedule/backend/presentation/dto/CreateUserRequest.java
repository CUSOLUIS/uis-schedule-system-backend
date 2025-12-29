package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new user.
 * Contains validation constraints for user creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 256, message = "Name must be between 2 and 256 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 250, message = "Email must not exceed 250 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 256, message = "Password must be between 8 and 256 characters")
    private String password;

    @NotBlank(message = "Role is required")
    @Size(max = 64, message = "Role must not exceed 64 characters")
    private String role;
}
