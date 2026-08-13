package com.uis.schedule.backend.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uis.schedule.backend.presentation.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * Handles 403 Forbidden responses when an authenticated user
 * lacks the required role (e.g. @PreAuthorize fails).
 *
 * Returns a consistent {@link ApiResponse} JSON envelope.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> apiResponse = ApiResponse.error(
                "You do not have sufficient permissions to perform this action",
                Collections.singletonList(accessDeniedException.getMessage())
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
