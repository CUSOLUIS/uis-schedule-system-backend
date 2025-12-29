package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing user.
 * All fields are optional to allow partial updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 2, max = 256, message = "Name must be between 2 and 256 characters")
    private String name;

    @Email(message = "Email must be valid")
    @Size(max = 250, message = "Email must not exceed 250 characters")
    private String email;

    @Size(max = 64, message = "Role must not exceed 64 characters")
    private String role;

    private Boolean active;
}
