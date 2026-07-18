package com.uis.schedule.backend.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.presentation.dto.AuthLoginRequest;
import com.uis.schedule.backend.presentation.dto.AuthResponse;
import com.uis.schedule.backend.presentation.dto.AuthSignupRequest;
import com.uis.schedule.backend.presentation.dto.ChangePasswordRequest;
import com.uis.schedule.backend.presentation.dto.ForgotPasswordRequest;
import com.uis.schedule.backend.presentation.dto.ResetPasswordRequest;
import com.uis.schedule.backend.service.interfaces.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@PreAuthorize("permitAll()")
public class AuthenticationController {
  private final UserService userService;

  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "Register a new user", description = "Creates a new user in the system and returns a JWT token upon successful registration.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or user with the same email already exists"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error during registration")
  })
  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<AuthResponse>> signUp(
      @Valid @RequestBody(required = true) AuthSignupRequest authSignupRequest) {
    AuthResponse authResponse = userService.signUp(authSignupRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(authResponse, "User registered successfully"));
  }

  @Operation(summary = "Authenticate a user", description = "Logs in a user with email/username and password, and returns a JWT token.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad credentials"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
  })
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(
      @Valid @RequestBody(required = true) AuthLoginRequest authLoginRequest) {
    AuthResponse authResponse = userService.login(authLoginRequest.usernameOrEmail(), authLoginRequest.password());
    return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
  }

  @Operation(summary = "Solicitar recuperación de contraseña", description = "Genera y envía un enlace de recuperación al correo si existe en el sistema.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Solicitud procesada"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Correo inválido")
  })
  @PostMapping("/password/forgot")
  public ResponseEntity<ApiResponse<Object>> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
    userService.requestPasswordReset(forgotPasswordRequest.email());
    return ResponseEntity.ok(ApiResponse.success(null,
        "Si el correo está registrado, recibirás instrucciones para recuperar tu contraseña"));
  }

  @Operation(summary = "Restablecer contraseña", description = "Restablece la contraseña usando el token de recuperación enviado por correo.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token inválido o expirado")
  })
  @PostMapping("/password/reset/{token}")
  public ResponseEntity<ApiResponse<Object>> resetPassword(
      @PathVariable String token,
      @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
    userService.resetPassword(token, resetPasswordRequest.newPassword());
    return ResponseEntity.ok(ApiResponse.success(null, "Contraseña restablecida correctamente"));
  }

  @Operation(summary = "Cambiar contraseña autenticada", description = "Permite al usuario autenticado cambiar su contraseña actual.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contraseña cambiada"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validación inválida"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
  })
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/password/change")
  public ResponseEntity<ApiResponse<Object>> changePassword(
      @AuthenticationPrincipal UserDetails userDetails,
      @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    userService.changePassword(
        userDetails.getUsername(),
        changePasswordRequest.currentPassword(),
        changePasswordRequest.newPassword());

    return ResponseEntity.ok(ApiResponse.success(null, "Contraseña actualizada correctamente"));
  }
}
