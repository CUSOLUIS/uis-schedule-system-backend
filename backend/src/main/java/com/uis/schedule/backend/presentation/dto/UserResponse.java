package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

import java.time.LocalDateTime;

/**
 * DTO for user response after creation or update.
 * Contains essential user information without sensitive data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Set<String> roles;
    private boolean active;
    private LocalDateTime lastSession;
}
