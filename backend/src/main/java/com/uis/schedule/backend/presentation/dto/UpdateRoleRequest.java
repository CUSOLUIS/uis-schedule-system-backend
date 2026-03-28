package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing role.
 * Fields are optional since it's an update (patch-like behavior).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {

    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    private String name;

    private Boolean isActive;
}
