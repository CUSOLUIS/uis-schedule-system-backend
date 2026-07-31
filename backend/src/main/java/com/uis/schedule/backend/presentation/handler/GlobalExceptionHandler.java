package com.uis.schedule.backend.presentation.handler;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.service.exception.InvalidTokenException;
import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.service.exception.InvitationNotFoundException;
import com.uis.schedule.backend.service.exception.InvitationConflictException;
import com.uis.schedule.backend.service.exception.RoleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UserNotFoundException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(ex.getMessage(), Collections.singletonList(ex.getMessage())),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvitationNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleInvitationNotFoundException(InvitationNotFoundException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(ex.getMessage(), Collections.singletonList(ex.getMessage())),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(RoleNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleRoleNotFoundException(RoleNotFoundException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(ex.getMessage(), Collections.singletonList(ex.getMessage())),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(InvalidTokenException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(ex.getMessage(), Collections.singletonList(ex.getMessage())),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvitationConflictException.class)
  public ResponseEntity<ApiResponse<Object>> handleInvitationConflictException(InvitationConflictException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(ex.getMessage(), Collections.singletonList(ex.getMessage())),
        HttpStatus.CONFLICT);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
    String message = "No tienes permisos para realizar esta acción";
    return new ResponseEntity<>(
        ApiResponse.error(message, Collections.singletonList(message)),
        HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(ex.getMessage(), Collections.singletonList(ex.getMessage())),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
  public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
      org.springframework.security.core.AuthenticationException ex) {
    String message = "Usuario o contraseña incorrectos";
    return new ResponseEntity<>(
        ApiResponse.error(message, Collections.singletonList(message)),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .toList();

    return new ResponseEntity<>(
        ApiResponse.error(ex.getMessage(), errors),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
    String message = "An internal server error occurred.";
    return new ResponseEntity<>(
        ApiResponse.error(message, Collections.singletonList(ex.getMessage())),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}