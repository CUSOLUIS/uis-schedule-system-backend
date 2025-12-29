package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for listing users with minimal information.
 * Used in tables and list views in the frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean active;
}
