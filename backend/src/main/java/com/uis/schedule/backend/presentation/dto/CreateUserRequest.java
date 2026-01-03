package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[^0-9]*$", message = "First name cannot contain numbers")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[^0-9]*$", message = "Last name cannot contain numbers")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 250, message = "Email must not exceed 250 characters")
    private String email;

        @NotBlank(message = "Password is required")

        @Size(min = 8, max = 256, message = "Password must be between 8 and 256 characters")

        private String password;

    

        

    }
