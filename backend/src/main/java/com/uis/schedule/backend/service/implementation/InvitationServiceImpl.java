package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.entity.UserInvitationEntity;
import com.uis.schedule.backend.persistence.repository.RoleRepository;
import com.uis.schedule.backend.persistence.repository.UserInvitationRepository;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.InvitationNotFoundException;
import com.uis.schedule.backend.service.exception.InvalidTokenException;
import com.uis.schedule.backend.service.interfaces.InvitationService;
import com.uis.schedule.backend.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final UserInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.invitation.expiration-hours}")
    private int expirationHours;

    @Override
    @Transactional
    public InvitationResponse createInvitation(CreateInvitationRequest request, UUID adminId) {

        // Bloquea si ya hay una invitación pendiente o completada esperando aprobación
        if (invitationRepository.existsByEmailAndStatus(request.getEmail(), "PENDING") ||
            invitationRepository.existsByEmailAndStatus(request.getEmail(), "COMPLETED")) {
            throw new RuntimeException("Ya existe una invitación activa para: " + request.getEmail());
        }

        // Bloquea si el correo ya pertenece a un usuario registrado
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new RuntimeException("Ya existe un usuario registrado con ese correo.");
        }

        // Obtiene el admin que está creando la invitación
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        // Genera el token único para el enlace
        String token = tokenGenerator.generateToken();

        // Construye y guarda la invitación
        UserInvitationEntity invitation = UserInvitationEntity.builder()
                .email(request.getEmail().toLowerCase())
                .token(token)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(expirationHours))
                .createdBy(admin)
                .build();

        invitationRepository.save(invitation);

        // Envía el correo con el enlace
        emailService.sendInvitationEmail(request.getEmail(), token);

        return mapToResponse(invitation);
    }

    @Override
    public InvitationResponse validateToken(String token) {

        UserInvitationEntity invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("El enlace no es válido"));

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("El enlace ha expirado");
        }

        if (!invitation.getStatus().equals("PENDING")) {
            throw new InvalidTokenException("Este enlace ya fue utilizado");
        }

        return mapToResponse(invitation);
    }

    @Override
    @Transactional
    public InvitationResponse completeInvitation(String token, CompleteInvitationRequest request) {

        UserInvitationEntity invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("El enlace no es válido"));

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("El enlace ha expirado");
        }

        if (!invitation.getStatus().equals("PENDING")) {
            throw new InvalidTokenException("Este enlace ya fue utilizado");
        }

        // Verifica que el username no esté en uso
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        invitation.setStatus("COMPLETED");
        invitation.setCompletedAt(LocalDateTime.now());
        invitation.setFirstName(request.getFirstName());
        invitation.setLastName(request.getLastName());
        invitation.setUsername(request.getUsername());
        invitation.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        invitationRepository.save(invitation);

        return mapToResponse(invitation);
    }

    @Override
    public List<InvitationResponse> getPendingApprovals() {
        return invitationRepository.findByStatus("COMPLETED")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvitationResponse approveInvitation(UUID invitationId, ApproveInvitationRequest request) {

        UserInvitationEntity invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException("Invitación no encontrada"));

        if (!invitation.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("La invitación no está en estado COMPLETED");
        }

        RoleEntity role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + request.getRoleName()));

        UserEntity newUser = UserEntity.builder()
                .email(invitation.getEmail())
                .firstName(invitation.getFirstName())
                .lastName(invitation.getLastName())
                .username(invitation.getUsername())
                .password(invitation.getPasswordHash())
                .isEnable(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();

        newUser.getRoles().add(role);
        userRepository.save(newUser);

        invitation.setStatus("APPROVED");
        invitationRepository.save(invitation);

        return mapToResponse(invitation);
    }

    @Override
    @Transactional
    public InvitationResponse rejectInvitation(UUID invitationId) {

        UserInvitationEntity invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException("Invitación no encontrada"));

        invitation.setStatus("REJECTED");
        invitationRepository.save(invitation);

        return mapToResponse(invitation);
    }

    // Convierte la entidad a DTO de respuesta
    private InvitationResponse mapToResponse(UserInvitationEntity invitation) {
        return InvitationResponse.builder()
                .invitationId(invitation.getInvitationId())
                .email(invitation.getEmail())
                .status(invitation.getStatus())
                .createdAt(invitation.getCreatedAt())
                .expiresAt(invitation.getExpiresAt())
                .completedAt(invitation.getCompletedAt())
                .createdByName(invitation.getCreatedBy().getFirstName()
                        + " " + invitation.getCreatedBy().getLastName())
                .build();
    }
}