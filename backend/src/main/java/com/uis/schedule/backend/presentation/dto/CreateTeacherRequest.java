package com.uis.schedule.backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTeacherRequest {

    @NotBlank
    @Schema(example = "María Pérez")
    private String fullName;

    @NotBlank
    @Pattern(regexp = "PLANTA|CATEDRA", message = "linkType must be PLANTA or CATEDRA")
    @Schema(allowableValues = {"PLANTA", "CATEDRA"}, example = "PLANTA")
    private String linkType;

    @Schema(example = "Lunes a viernes 8-12")
    private String availability;

    @Schema(example = "Escuela de Historia")
    private String department;
}
