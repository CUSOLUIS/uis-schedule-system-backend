package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for listing class hours with minimal information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassHourListDTO {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long groupId;
    private Long classroomId;
    private Long dayId;
    private boolean isActive;
}
