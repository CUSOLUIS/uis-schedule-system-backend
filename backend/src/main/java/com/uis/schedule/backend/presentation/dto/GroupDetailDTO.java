package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detailed DTO for retrieving a specific group.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailDTO {
    private Long id;
    private String name;
    private Integer capacity;
    private Long classroomId;
    private Long teacherId;
    private Long periodId;
    private Long subjectId;
    private boolean isActive;
}
