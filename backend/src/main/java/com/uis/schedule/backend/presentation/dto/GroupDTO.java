package com.uis.schedule.backend.presentation.dto;

import lombok.*;

import java.util.UUID;

/**
 * Generic DTO for Group entity.
 * Used for backward compatibility and non-CRUD representations.
 */
@Getter
@Setter
@AllArgsConstructor
public class GroupDTO {
    private UUID groupId;
    private String name;
    private Integer capacity;
    private TeacherDTO teacher;
    private AcademicPeriodDTO period;
    private SubjectDTO subject;
    private UUID classroomId;
    private boolean isActive;
}
