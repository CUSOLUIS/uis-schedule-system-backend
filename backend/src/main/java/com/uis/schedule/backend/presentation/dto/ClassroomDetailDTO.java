package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detailed DTO for retrieving a specific classroom.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDetailDTO {
    private Long classroomId;
    private String number;
    private Integer maxCapacity;
    private String building;
    private String campus;
    private String type;
    private boolean isActive;
}
