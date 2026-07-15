package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for listing groups with minimal information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupListDTO {
    private Long id;
    private String name;
    private Integer capacity;
    private Long classroomId;
    private Long teacherId;
    private Long periodId;
    private Long subjectId;
    private boolean isActive;
}
