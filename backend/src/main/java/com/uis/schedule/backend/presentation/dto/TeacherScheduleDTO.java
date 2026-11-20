package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherScheduleDTO {
    private UUID teacherId;
    private String teacherName;
    private String linkType;
    private String department;
    private List<TeacherScheduleEntryDTO> entries;
}
