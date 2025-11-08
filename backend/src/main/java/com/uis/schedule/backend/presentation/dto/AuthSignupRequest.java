package com.uis.schedule.backend.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthSignupRequest(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String contactNumber,
        @NotBlank String password) {
}
