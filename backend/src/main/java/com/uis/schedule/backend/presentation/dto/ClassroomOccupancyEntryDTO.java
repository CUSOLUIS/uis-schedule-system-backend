package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

/**
 * One occupied slot in a classroom weekly grid.
 * Field order matches the JPQL constructor — do not reorder.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomOccupancyEntryDTO {
    private UUID classHourId;
    private String dayName;
    private LocalTime startTime;
    private LocalTime endTime;
    private UUID groupId;
    private String groupName;
    private Integer groupCapacity;
    private String subjectCode;
    private String subjectName;
    private UUID teacherId;
    private String teacherName;
}
