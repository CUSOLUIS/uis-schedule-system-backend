package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBuildingRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String faculty;

    @NotNull
    private Integer preferenceOrder;

    private Boolean hasAc;

    private Boolean hasComputerLab;

    private String campus;
}
