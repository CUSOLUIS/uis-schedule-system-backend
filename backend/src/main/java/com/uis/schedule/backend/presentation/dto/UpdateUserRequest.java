package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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

    @Size(min = 2, max = 128, message = "First name must be between 2 and 128 characters")
    @Pattern(regexp = "^[^0-9]*$", message = "First name cannot contain numbers")
    private String firstName;

    @Size(min = 2, max = 128, message = "Last name must be between 2 and 128 characters")
    @Pattern(regexp = "^[^0-9]*$", message = "Last name cannot contain numbers")
    private String lastName;

    @Email(message = "Email must be valid")
    @Size(max = 250, message = "Email must not exceed 250 characters")
    private String email;

    private Boolean active;
}
