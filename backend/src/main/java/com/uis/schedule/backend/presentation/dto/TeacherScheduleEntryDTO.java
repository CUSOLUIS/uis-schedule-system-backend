package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Una franja del horario del profesor en un día concreto.
 * El orden de los campos define el constructor usado por la consulta JPQL
 * de {@code ClassHourRepository.findScheduleEntriesByTeacherId}: no reordenar.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherScheduleEntryDTO {
    private UUID classHourId;
    private String dayName;
    private LocalTime startTime;
    private LocalTime endTime;
    private UUID groupId;
    private String groupName;
    private Integer groupCapacity;
    private String subjectCode;
    private String subjectName;
    private Integer subjectCredits;
    private String classroomNumber;
    private String building;
}
