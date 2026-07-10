package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

// DTO con los datos que diligencia la persona cuando abre el enlace
@Getter
@Setter
public class CompleteInvitationRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[^0-9]*$", message = "El nombre no puede contener números")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = "^[^0-9]*$", message = "El apellido no puede contener números")
    private String lastName;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}