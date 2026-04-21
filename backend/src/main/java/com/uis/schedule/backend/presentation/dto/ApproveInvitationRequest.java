package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// DTO que usa el admin cuando aprueba una solicitud y asigna el rol
@Getter
@Setter
public class ApproveInvitationRequest {

    // Nombre del rol a asignar: DOCENTE, OPERADOR, etc.
    @NotBlank(message = "El rol es obligatorio")
    private String roleName;
}