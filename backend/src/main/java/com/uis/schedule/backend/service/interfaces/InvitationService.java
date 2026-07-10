package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.*;
import java.util.List;
import java.util.UUID;

// Contrato del servicio de invitaciones
public interface InvitationService {

    // Crea la invitación y envía el correo
    InvitationResponse createInvitation(CreateInvitationRequest request, UUID adminId);

    // Valida que el token sea válido y no haya expirado
    InvitationResponse validateToken(String token);

    // Guarda los datos que diligencia la persona invitada
    InvitationResponse completeInvitation(String token, CompleteInvitationRequest request);

    // Retorna todas las invitaciones en estado COMPLETED (lista de espera)
    List<InvitationResponse> getPendingApprovals();

    // Aprueba la solicitud, asigna rol y crea el usuario activo
    InvitationResponse approveInvitation(UUID invitationId, ApproveInvitationRequest request);

    // Rechaza la solicitud
    InvitationResponse rejectInvitation(UUID invitationId);
}