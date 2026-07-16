package com.uis.schedule.backend.presentation.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

/**
 * Centralized exception handler for all controllers.
 * Every exception is converted to a consistent {@link ApiResponse} envelope.
 *
 * <p>Response format (all cases):
 * <pre>
 * {
 *   "Data": null,
 *   "Message": "Human-readable message",
 *   "Errors": ["detail1", "detail2"]
 * }
 * </pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── 404 — Resource not found ────────────────────

    @ExceptionHandler(ClassroomNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleClassroomNotFoundException(ClassroomNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleGroupNotFoundException(GroupNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ClassHourNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleClassHourNotFoundException(ClassHourNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UserNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleRoleNotFoundException(RoleNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvitationNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvitationNotFoundException(InvitationNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ─── 400 — Bad request / Validation errors ───────

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(InvalidTokenException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        return new ResponseEntity<>(
                ApiResponse.error("Validation failed", errors),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String detail = "Invalid request body format";
        if (ex.getCause() instanceof InvalidFormatException ife) {
            detail = "Invalid value for field: " + ife.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : String.valueOf(ref.getIndex()))
                    .reduce((a, b) -> a + "." + b)
                    .orElse("unknown");
        }
        return build(HttpStatus.BAD_REQUEST, detail);
    }

    // ─── 403 — Access denied (handled by Security too, but as a safety net) ──

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "You do not have sufficient permissions to perform this action");
    }

    // ─── 500 — Catch-all (never expose internal details) ──

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred");
    }

    // ─── Helper ──────────────────────────────────────

    private ResponseEntity<ApiResponse<Object>> build(HttpStatus status, String message) {
        return new ResponseEntity<>(
                ApiResponse.error(message, Collections.singletonList(message)),
                status);
    }
}
