package com.uis.schedule.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String fullName;
    private String email;
    private RoleDTO role; // OBJETO COMPLETO, no solo ID
}