package com.uis.schedule.backend.presentation.dto;

import lombok.*;

/**
 * Generic DTO for Classroom entity.
 * Used for backward compatibility and non-CRUD representations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {
    private Long id;
    private String number;
    private Integer maxCapacity;
    private String building;
    private String campus;
    private String type;
    private boolean isActive;
}
