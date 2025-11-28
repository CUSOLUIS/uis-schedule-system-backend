package com.uis.schedule.backend.dto;

import com.uis.schedule.backend.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private UserDTO user;
    private String token;
    private String message;
}