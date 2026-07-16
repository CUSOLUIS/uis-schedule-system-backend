package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Detailed DTO for retrieving a specific class hour.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassHourDetailDTO {
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
