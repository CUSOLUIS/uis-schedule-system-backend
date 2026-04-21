package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// DTO que recibe el administrador cuando registra un correo para invitar
@Getter
@Setter
public class CreateInvitationRequest {

    // Correo de la persona a invitar
    @Email(message = "El correo no tiene un formato válido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;
}