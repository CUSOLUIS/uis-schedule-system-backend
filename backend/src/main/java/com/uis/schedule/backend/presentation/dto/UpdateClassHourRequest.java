package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Request DTO for updating an existing class hour.
 * Fields are optional since it's an update (patch-like behavior).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassHourRequest {

    private Long groupId;

    private Long classroomId;

    private Long dayId;

    private LocalTime hour;
}
