package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Response DTO returned after creating or updating a class hour.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassHourResponse {
    private Long id;
    private LocalTime hour;
    private Long groupId;
    private Long classroomId;
    private Long dayId;
    private boolean isActive;
}
