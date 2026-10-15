package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.entity.UserInvitationEntity;
import com.uis.schedule.backend.persistence.repository.RoleRepository;
import com.uis.schedule.backend.persistence.repository.UserInvitationRepository;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.ApproveInvitationRequest;
import com.uis.schedule.backend.util.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvitationServiceImplTest {

    @Mock
    private UserInvitationRepository invitationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private TokenGenerator tokenGenerator;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TeacherProfileService teacherProfileService;

    private InvitationServiceImpl invitationService;

    @BeforeEach
    void setUp() {
        invitationService = new InvitationServiceImpl(
                invitationRepository,
                userRepository,
                roleRepository,
                tokenGenerator,
                emailService,
                passwordEncoder,
                teacherProfileService);
    }

    @Test
    void approveInvitation_withTeacherRole_ensuresTeacherProfile() {
        UUID invitationId = UUID.randomUUID();
        UserInvitationEntity invitation = UserInvitationEntity.builder()
                .invitationId(invitationId)
                .email("docente@uis.edu")
                .token("token")
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .firstName("Luis")
                .lastName("Perez")
                .username("lperez")
                .passwordHash("hash")
                .createdBy(UserEntity.builder().firstName("Admin").lastName("Root").build())
                .build();

        RoleEntity teacher = RoleEntity.builder().name("TEACHER").isActive(true).build();
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(roleRepository.findByName("TEACHER")).thenReturn(Optional.of(teacher));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity saved = invocation.getArgument(0);
            saved.setUserId(UUID.randomUUID());
            return saved;
        });

        ApproveInvitationRequest request = new ApproveInvitationRequest();
        request.setRoleName("TEACHER");

        invitationService.approveInvitation(invitationId, request);

        verify(teacherProfileService).ensureFor(any(UserEntity.class));
    }
}
