package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.presentation.dto.AuthLoginRequest;
import com.uis.schedule.backend.presentation.dto.AuthResponse;
import com.uis.schedule.backend.presentation.dto.AuthSignupRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uis.schedule.backend.service.interfaces.UserService;

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
}
