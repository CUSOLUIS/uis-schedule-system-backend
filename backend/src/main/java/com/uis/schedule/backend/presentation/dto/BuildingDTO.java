package com.uis.schedule.backend.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingDTO {
    private UUID id;
    private String code;
    private String name;
    private String faculty;
    private Integer preferenceOrder;
    @JsonProperty("hasAc")
    private boolean hasAc;
    @JsonProperty("hasComputerLab")
    private boolean hasComputerLab;
    private String campus;
    @JsonProperty("isActive")
    private boolean isActive;
}
