package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after creating or updating a group.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    private UUID id;
    private String name;
    private Integer capacity;
    private UUID classroomId;
    private UUID teacherId;
    private UUID periodId;
    private UUID subjectId;
    private boolean isActive;
}
