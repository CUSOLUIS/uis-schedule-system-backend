package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for listing roles with minimal information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleListDTO {
    private UUID guid;
    private String name;
    private boolean isActive;
}
