package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Horario semanal de un profesor. {@code entries} viene vacío (nunca null)
 * cuando el profesor no tiene grupos con franjas activas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherScheduleDTO {
    private UUID teacherId;
    private String teacherName;
    private String department;
    private List<TeacherScheduleEntryDTO> entries;
}
