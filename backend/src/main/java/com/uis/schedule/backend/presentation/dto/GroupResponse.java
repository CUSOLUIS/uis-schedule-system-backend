package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after creating or updating a group.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private Integer capacity;
    private Long classroomId;
    private Long teacherId;
    private Long periodId;
    private Long subjectId;
    private boolean isActive;
}
