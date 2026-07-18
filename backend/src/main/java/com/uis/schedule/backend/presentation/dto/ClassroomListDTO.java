package com.uis.schedule.backend.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for listing classrooms with minimal information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomListDTO {
    private UUID id;
    private String number;
    private Integer maxCapacity;
    private String building;
    private String campus;
    private String type;
    @JsonProperty("isActive")
    private boolean isActive;
}
