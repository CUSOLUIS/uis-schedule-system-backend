package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String currentPassword,
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, max = 256, message = "La contraseña debe tener entre 8 y 256 caracteres")
        String newPassword) {
}
