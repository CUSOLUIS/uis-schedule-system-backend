package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthSignupRequest(
        @NotBlank(message = "First name is required")
        @Pattern(regexp = "^[^0-9]*$", message = "First name cannot contain numbers")
        @Size(min = 2, max = 128, message = "First name must be between 2 and 128 characters")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Pattern(regexp = "^[^0-9]*$", message = "Last name cannot contain numbers")
        @Size(min = 2, max = 128, message = "Last name must be between 2 and 128 characters")
        String lastName,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 250, message = "Email must not exceed 250 characters")
        String email,
        @NotBlank String contactNumber,
        @NotBlank String password) {
}
