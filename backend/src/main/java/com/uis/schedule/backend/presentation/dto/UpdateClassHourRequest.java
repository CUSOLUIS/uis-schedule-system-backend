package com.uis.schedule.backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassHourRequest {

    private UUID groupId;

    private UUID classroomId;

    private UUID teacherId;

    @Schema(allowableValues = {"PENDING", "ASSIGNED", "NO_CLASSROOM_REQUIRED", "SENT_TO_ADMISSIONS"})
    @Pattern(regexp = "PENDING|ASSIGNED|NO_CLASSROOM_REQUIRED|SENT_TO_ADMISSIONS",
            message = "Invalid session status")
    private String status;

    private List<UUID> dayIds;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isActive;

    /** When true, clears the classroom and marks the session as NO_CLASSROOM_REQUIRED. */
    private Boolean clearClassroom;
}
