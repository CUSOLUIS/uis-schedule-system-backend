package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Detailed DTO for retrieving a specific class hour.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassHourDetailDTO {
    private Long id;
    private LocalTime hour;
    private Long groupId;
    private Long classroomId;
    private Long dayId;
    private boolean isActive;
}
