package com.uis.schedule.backend.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after creating or updating a classroom.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomResponse {
    private UUID id;
    private String number;
    private Integer maxCapacity;
    private String building;
    private String campus;
    private String type;
    @JsonProperty("isActive")
    private boolean isActive;
}
