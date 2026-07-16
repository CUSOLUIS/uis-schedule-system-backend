package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO returned after creating or updating a class hour.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassHourResponse {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long groupId;
    private Long classroomId;
    private List<Long> dayIds;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
}
