package com.uis.schedule.backend.presentation.dto;

import lombok.*;

import java.util.UUID;

/**
 * Generic DTO for Classroom entity.
 * Used for backward compatibility and non-CRUD representations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {
    private UUID id;
    private String number;
    private Integer maxCapacity;
    private String building;
    private String campus;
    private String type;
    private boolean isActive;
}
