package com.uis.schedule.backend.presentation.handler;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.service.exception.InvalidTokenException;
import com.uis.schedule.backend.service.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(InvalidTokenException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(ex.getMessage(), Collections.singletonList(ex.getMessage())),
        HttpStatus.UNAUTHORIZED);
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
