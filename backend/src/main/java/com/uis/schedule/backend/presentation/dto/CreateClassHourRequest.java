package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new class hour.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassHourRequest {

    @NotNull(message = "Group ID is required")
    private UUID groupId;

    @NotNull(message = "Classroom ID is required")
    private UUID classroomId;

    @NotEmpty(message = "At least one day ID is required")
    private List<UUID> dayIds;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}
