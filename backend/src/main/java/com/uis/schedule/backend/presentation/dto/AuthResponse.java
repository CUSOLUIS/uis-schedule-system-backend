package com.uis.schedule.backend.presentation.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"token", "message"})
public record AuthResponse(
        String token,
        String message) {
}