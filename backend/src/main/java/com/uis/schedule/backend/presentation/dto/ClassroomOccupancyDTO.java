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
public class ClassroomOccupancyDTO {
    private UUID classroomId;
    private String number;
    private Integer maxCapacity;
    private String buildingCode;
    private String buildingName;
    private String campus;
    private List<ClassroomOccupancyEntryDTO> entries;
}
