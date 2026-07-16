package com.uis.schedule.backend.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uis.schedule.backend.presentation.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * Handles 401 Unauthorized responses when a request requires
 * authentication but no valid token is provided (or the token is expired).
 *
 * Returns a consistent {@link ApiResponse} JSON envelope.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> apiResponse = ApiResponse.error(
                "Authentication required: please provide a valid token",
                Collections.singletonList(authException.getMessage())
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
