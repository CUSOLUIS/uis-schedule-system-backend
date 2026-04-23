package com.uis.schedule.backend.presentation.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

// DTO de respuesta que se devuelve al consultar una invitación
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationResponse {

    private UUID invitationId;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime completedAt;

    // Nombre del admin que creó la invitación
    private String createdByName;
}