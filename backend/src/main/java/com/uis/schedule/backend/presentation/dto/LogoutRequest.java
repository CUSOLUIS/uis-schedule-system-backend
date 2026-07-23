package com.uis.schedule.backend.presentation.dto;

/**
 * Cuerpo opcional para {@code POST /auth/logout}.
 */
public record LogoutRequest(String refreshToken) {
}
