package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing group.
 * Fields are optional since it's an update (patch-like behavior).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupRequest {

    @Size(max = 250, message = "Group name must be at most 250 characters")
    private String name;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Long classroomId;

    private Long teacherId;

    private Long periodId;

    private Long subjectId;
}
