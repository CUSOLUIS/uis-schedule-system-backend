package com.uis.schedule.backend.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBuildingRequest {
    private String code;
    private String name;
    private String faculty;
    private Integer preferenceOrder;
    private Boolean hasAc;
    private Boolean hasComputerLab;
    private String campus;
    private Boolean active;
}
