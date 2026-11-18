package com.uis.schedule.backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
 * Classroom is optional (consultorios / trabajos de grado / sin aula).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassHourRequest {

    @NotNull(message = "Group ID is required")
    private UUID groupId;

    @Schema(description = "Optional classroom. Null means no classroom required.")
    private UUID classroomId;

    @Schema(description = "Teacher for this session. Falls back to group teacher when omitted.")
    private UUID teacherId;

    @Schema(allowableValues = {"PENDING", "ASSIGNED", "NO_CLASSROOM_REQUIRED", "SENT_TO_ADMISSIONS"})
    @Pattern(regexp = "PENDING|ASSIGNED|NO_CLASSROOM_REQUIRED|SENT_TO_ADMISSIONS",
            message = "Invalid session status")
    private String status;

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
