package com.uis.schedule.backend.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for listing groups with minimal information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupListDTO {
    private UUID id;
    private String name;
    private Integer capacity;
    private UUID classroomId;
    private UUID teacherId;
    private UUID periodId;
    private UUID subjectId;
    @JsonProperty("isActive")
    private boolean isActive;
}
