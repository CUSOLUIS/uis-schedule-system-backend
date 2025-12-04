package com.uis.schedule.backend.presentation.controller;

import java.util.Map;

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
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap) {
        try {
            return userService.signUp(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RequestResponseUtils.getResponseEntity("Algo salió mal", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthLoginRequest request) {
        try {
            return userService.login(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RequestResponseUtils.getResponseEntity("Algo salió mal", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
