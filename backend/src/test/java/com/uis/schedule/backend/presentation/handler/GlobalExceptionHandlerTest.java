package com.uis.schedule.backend.presentation.handler;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.service.exception.InvalidSessionTokenException;
import com.uis.schedule.backend.service.exception.ProtectedRoleException;
import com.uis.schedule.backend.service.exception.RoleInUseException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

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

    @Test
    void invalidSessionToken_returns401() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleInvalidSessionTokenException(
                new InvalidSessionTokenException("Refresh token revocado o expirado"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Refresh token revocado o expirado", response.getBody().getMessage());
    }

    @Test
    void malformedIdentifier_returns400() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleMethodArgumentTypeMismatch(
                new MethodArgumentTypeMismatchException(
                        "not-a-uuid", UUID.class, "id", null, new IllegalArgumentException("bad uuid")));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid value for 'id'.", response.getBody().getMessage());
    }

    @Test
    void unknownRoute_returns404() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleUnknownRoute(
                new NoResourceFoundException(HttpMethod.GET, "/api/does-not-exist"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void dataIntegrity_returns409() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("duplicate key"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The operation violates a uniqueness or integrity constraint.",
                response.getBody().getMessage());
    }
}
