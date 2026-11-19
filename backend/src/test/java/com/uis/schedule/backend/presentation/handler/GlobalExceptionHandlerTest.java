package com.uis.schedule.backend.presentation.handler;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.service.exception.InvalidSessionTokenException;
import com.uis.schedule.backend.service.exception.InvalidTokenException;
import com.uis.schedule.backend.service.exception.ProtectedRoleException;
import com.uis.schedule.backend.service.exception.RoleInUseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void illegalState_returns500WithoutLeakingMessage() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleIllegalStateException(
                new IllegalStateException("Mail transport failed"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An internal server error occurred", response.getBody().getMessage());
    }

    @Test
    void invalidSessionToken_returns401() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleInvalidSessionTokenException(
                new InvalidSessionTokenException("Refresh token revocado o expirado"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Refresh token revocado o expirado", response.getBody().getMessage());
    }

    @Test
    void invalidInvitationToken_returns400() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleInvalidTokenException(
                new InvalidTokenException("El enlace no es válido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El enlace no es válido", response.getBody().getMessage());
    }

    @Test
    void protectedRole_returns403() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleProtectedRoleException(
                new ProtectedRoleException("Security protection: The ADMINISTRATOR role cannot be deleted."));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Security protection: The ADMINISTRATOR role cannot be deleted.",
                response.getBody().getMessage());
    }

    @Test
    void roleInUse_returns409() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleConflictExceptions(
                new RoleInUseException("Role is assigned to one or more users and cannot be deleted."));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Role is assigned to one or more users and cannot be deleted.",
                response.getBody().getMessage());
    }
}
