package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new classroom.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassroomRequest {

    @NotBlank(message = "Classroom number is required")
    @Size(max = 50, message = "Classroom number must be at most 50 characters")
    private String number;

    @NotNull(message = "Max capacity is required")
    @Min(value = 1, message = "Max capacity must be at least 1")
    private Integer maxCapacity;

    @Size(max = 256, message = "Building name must be at most 256 characters")
    private String building;

    @Size(max = 256, message = "Campus name must be at most 256 characters")
    private String campus;

    @Size(max = 100, message = "Classroom type must be at most 100 characters")
    private String type;
}
