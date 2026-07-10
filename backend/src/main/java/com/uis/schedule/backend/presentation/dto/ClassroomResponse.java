package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after creating or updating a classroom.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomResponse {
    private Long classroomId;
    private String number;
    private Integer maxCapacity;
    private String building;
    private String campus;
    private String type;
    private boolean isActive;
}
