package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
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
    private Long id;
    private String number;
    private Integer maxCapacity;
    private String building;
    private String campus;
    private String type;
    private boolean isActive;
}
