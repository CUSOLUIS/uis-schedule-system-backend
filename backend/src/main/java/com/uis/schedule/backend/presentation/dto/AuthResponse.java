package com.uis.schedule.backend.presentation.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"token", "refreshToken", "message"})
public record AuthResponse(
        String token,
        String refreshToken,
        String message) {
}
