package com.uis.schedule.backend.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO returned after creating or updating a class hour.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassHourResponse {
    private UUID id;
    private LocalTime startTime;
    private LocalTime endTime;
    private UUID groupId;
    private UUID classroomId;
    private List<String> dayNames;
    private LocalDate startDate;
    private LocalDate endDate;
    @JsonProperty("isActive")
    private boolean isActive;
}
