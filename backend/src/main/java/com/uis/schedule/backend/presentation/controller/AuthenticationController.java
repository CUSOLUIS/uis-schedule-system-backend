package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.AuthLoginRequest;
import com.uis.schedule.backend.presentation.dto.AuthSignupRequest;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.util.RequestResponseUtils;


@RestController
@RequestMapping("/auth")
@PreAuthorize("permitAll()")
public class AuthenticationController {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) AuthSignupRequest authSignupRequest) {
        try {
            return userService.signUp(authSignupRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RequestResponseUtils.getResponseEntity("Algo salió mal", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) AuthLoginRequest authLoginRequest) {
        try {
            return userService.login(authLoginRequest.usernameOrEmail(), authLoginRequest.password());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RequestResponseUtils.getResponseEntity("Algo salió mal", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
