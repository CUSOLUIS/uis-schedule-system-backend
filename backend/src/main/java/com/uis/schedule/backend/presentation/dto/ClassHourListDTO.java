package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for listing class hours with minimal information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassHourListDTO {
    private UUID id;
    private LocalTime startTime;
    private LocalTime endTime;
    private UUID groupId;
    private UUID classroomId;
    private List<String> dayNames;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
}
