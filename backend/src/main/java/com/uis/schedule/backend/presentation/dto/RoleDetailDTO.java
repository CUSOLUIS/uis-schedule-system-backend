package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Detailed DTO for retrieving a specific role.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDetailDTO {
    private UUID guid;
    private String name;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
