package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.service.interfaces.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for invitation management operations.
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/invitations")
@Tag(name = "Invitations", description = "Invitation management endpoints")
public class InvitationController {

    private final InvitationService invitationService;
    private final com.uis.schedule.backend.persistence.repository.UserRepository userRepository;

    public InvitationController(InvitationService invitationService,
                                com.uis.schedule.backend.persistence.repository.UserRepository userRepository) {
        this.invitationService = invitationService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Create a new invitation", description = "Creates a new invitation for a user. Requires ADMINISTRADOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Invitation created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid invitation data or email already invited"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<InvitationResponse>> createInvitation(
            @Valid @RequestBody CreateInvitationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID adminId = userRepository
                .findUserEntityByEmailOrUsername(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Admin user not found"))
                .getUserId();

        InvitationResponse response = invitationService.createInvitation(request, adminId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Invitation created successfully"));
    }

    @Operation(summary = "Validate invitation token", description = "Validates if an invitation token is still valid. Public endpoint.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token validation result"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invitation not found")
    })
    @GetMapping("/validate/{token}")
    public ResponseEntity<ApiResponse<InvitationResponse>> validateToken(@PathVariable String token) {
        InvitationResponse response = invitationService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success(response, "Token validated successfully"));
    }

    @Operation(summary = "Complete invitation registration", description = "Completes the registration for an invited user. Public endpoint.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or expired token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invitation not found")
    })
    @PostMapping("/complete/{token}")
    public ResponseEntity<ApiResponse<InvitationResponse>> completeInvitation(
            @PathVariable String token,
            @Valid @RequestBody CompleteInvitationRequest request) {
        InvitationResponse response = invitationService.completeInvitation(token, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Registration completed successfully"));
    }

    @Operation(summary = "Get pending invitations", description = "Retrieves all pending invitations. Requires ADMINISTRADOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved pending invitations"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getPendingApprovals() {
        List<InvitationResponse> pending = invitationService.getPendingApprovals();
        return ResponseEntity.ok(ApiResponse.success(pending, "Pending invitations retrieved successfully"));
    }

    @Operation(summary = "Approve an invitation", description = "Approves a pending invitation. Requires ADMINISTRADOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invitation approved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid approval data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invitation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{invitationId}/approve")
    public ResponseEntity<ApiResponse<InvitationResponse>> approveInvitation(
            @PathVariable UUID invitationId,
            @Valid @RequestBody ApproveInvitationRequest request) {
        InvitationResponse response = invitationService.approveInvitation(invitationId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Invitation approved successfully"));
    }

    @Operation(summary = "Reject an invitation", description = "Rejects a pending invitation. Requires ADMINISTRADOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invitation rejected successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invitation not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{invitationId}/reject")
    public ResponseEntity<ApiResponse<InvitationResponse>> rejectInvitation(@PathVariable UUID invitationId) {
        InvitationResponse response = invitationService.rejectInvitation(invitationId);
        return ResponseEntity.ok(ApiResponse.success(response, "Invitation rejected successfully"));
    }
}
