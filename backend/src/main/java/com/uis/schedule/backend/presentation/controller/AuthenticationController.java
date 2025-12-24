package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.AuthLoginRequest;
import com.uis.schedule.backend.presentation.dto.AuthResponse;
import com.uis.schedule.backend.presentation.dto.AuthSignupRequest;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.presentation.dto.AuthLoginRequest;
import com.uis.schedule.backend.util.RequestResponseUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@PreAuthorize("permitAll()")
public class AuthenticationController {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody(required = true) AuthSignupRequest authSignupRequest) {
        try {
            AuthResponse authResponse = userService.signUp(authSignupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(null, "Something went wrong", null, false));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody(required = true) AuthLoginRequest authLoginRequest) {
        try {
            AuthResponse authResponse = userService.login(authLoginRequest.usernameOrEmail(), authLoginRequest.password());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(null, "Something went wrong", null, false));
    }
}
