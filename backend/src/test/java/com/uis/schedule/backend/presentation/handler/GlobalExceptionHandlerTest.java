package com.uis.schedule.backend.presentation.handler;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.service.exception.ProtectedRoleException;
import com.uis.schedule.backend.service.exception.RoleInUseException;
import com.uis.schedule.backend.service.exception.TeacherNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
    void teacherNotFound_returns404() {
        UUID id = UUID.fromString("10000001-0000-0000-0000-000000000099");
        ResponseEntity<ApiResponse<Object>> response = handler.handleTeacherNotFoundException(
                new TeacherNotFoundException(id));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Teacher not found with ID: " + id, response.getBody().getMessage());
    }

    @Test
    void argumentTypeMismatch_returns400WithParameterName() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "no-es-uuid", UUID.class, "teacherId", null, new IllegalArgumentException("Invalid UUID"));

        ResponseEntity<ApiResponse<Object>> response = handler.handleMethodArgumentTypeMismatchException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid value for parameter 'teacherId'", response.getBody().getMessage());
    }
}
