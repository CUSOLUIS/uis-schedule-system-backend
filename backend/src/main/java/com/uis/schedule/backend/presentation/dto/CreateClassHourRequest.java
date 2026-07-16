package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Request DTO for creating a new class hour.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassHourRequest {

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    @NotNull(message = "Day ID is required")
    private Long dayId;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;
}
