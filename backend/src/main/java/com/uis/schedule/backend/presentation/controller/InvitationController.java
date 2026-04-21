package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.interfaces.InvitationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.uis.schedule.backend.persistence.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<InvitationResponse> createInvitation(
            @Valid @RequestBody CreateInvitationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID adminId = userRepository
                .findUserEntityByEmailOrUsername(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"))
                .getUserId();

        InvitationResponse response = invitationService.createInvitation(request, adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<InvitationResponse> validateToken(@PathVariable String token) {
        return ResponseEntity.ok(invitationService.validateToken(token));
    }

    @PostMapping("/complete/{token}")
    public ResponseEntity<InvitationResponse> completeInvitation(
            @PathVariable String token,
            @Valid @RequestBody CompleteInvitationRequest request) {
        return ResponseEntity.ok(invitationService.completeInvitation(token, request));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/pending")
    public ResponseEntity<List<InvitationResponse>> getPendingApprovals() {
        return ResponseEntity.ok(invitationService.getPendingApprovals());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{invitationId}/approve")
    public ResponseEntity<InvitationResponse> approveInvitation(
            @PathVariable UUID invitationId,
            @Valid @RequestBody ApproveInvitationRequest request) {
        return ResponseEntity.ok(invitationService.approveInvitation(invitationId, request));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{invitationId}/reject")
    public ResponseEntity<InvitationResponse> rejectInvitation(@PathVariable UUID invitationId) {
        return ResponseEntity.ok(invitationService.rejectInvitation(invitationId));
    }
}