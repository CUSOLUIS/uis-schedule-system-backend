package com.uis.schedule.backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeacherRequest {

    @Schema(example = "María Pérez")
    private String fullName;

    @Pattern(regexp = "PLANTA|CATEDRA", message = "linkType must be PLANTA or CATEDRA")
    @Schema(allowableValues = {"PLANTA", "CATEDRA"}, example = "CATEDRA")
    private String linkType;

    private String availability;

    private String department;
}
