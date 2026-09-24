package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.repository.RoleRepository;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.PaginatedResponse;
import com.uis.schedule.backend.presentation.dto.RoleListDTO;
import com.uis.schedule.backend.presentation.dto.UpdateRoleRequest;
import com.uis.schedule.backend.service.exception.ProtectedRoleException;
import com.uis.schedule.backend.service.exception.RoleInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(roleRepository, userRepository);
    }

    @Test
    void updateRole_rejectsModificationOfAdministrator() {
        UUID adminId = UUID.randomUUID();
        RoleEntity administrator = RoleEntity.builder()
                .guid(adminId)
                .name("ADMINISTRATOR")
                .isActive(true)
                .build();
        when(roleRepository.findById(adminId)).thenReturn(Optional.of(administrator));

        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .name("COORDINATOR")
                .isActive(false)
                .build();

        ProtectedRoleException ex = assertThrows(ProtectedRoleException.class,
                () -> roleService.updateRole(adminId, request));

        assertTrue(ex.getMessage().startsWith("Security protection"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteRole_rejectsAdministrator() {
        UUID adminId = UUID.randomUUID();
        RoleEntity administrator = RoleEntity.builder()
                .guid(adminId)
                .name("ADMINISTRATOR")
                .isActive(true)
                .build();
        when(roleRepository.findById(adminId)).thenReturn(Optional.of(administrator));

        ProtectedRoleException ex = assertThrows(ProtectedRoleException.class,
                () -> roleService.deleteRole(adminId));

        assertTrue(ex.getMessage().contains("cannot be deleted"));
        verify(userRepository, never()).existsByRoles_Guid(any());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteRole_rejectsWhenAssignedToUsers() {
        UUID roleId = UUID.randomUUID();
        RoleEntity teacher = RoleEntity.builder()
                .guid(roleId)
                .name("TEACHER")
                .isActive(true)
                .build();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(teacher));
        when(userRepository.existsByRoles_Guid(roleId)).thenReturn(true);

        RoleInUseException ex = assertThrows(RoleInUseException.class,
                () -> roleService.deleteRole(roleId));

        assertTrue(ex.getMessage().contains("assigned to"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void listRoles_propagatesRepositoryFailure() {
        when(roleRepository.findAllByIsActiveTrue(any(Pageable.class)))
                .thenThrow(new RuntimeException("db down"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> roleService.listRoles(0, 10));
        assertEquals("db down", ex.getMessage());
    }

    @Test
    void listAllRolesIncludingInactive_propagatesRepositoryFailure() {
        when(roleRepository.findAll(any(Pageable.class)))
                .thenThrow(new RuntimeException("db down"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roleService.listAllRolesIncludingInactive(0, 10));
        assertEquals("db down", ex.getMessage());
    }

    @Test
    void findByStatus_propagatesRepositoryFailure() {
        when(roleRepository.findAllByIsActive(true, PageRequest.of(0, 10)))
                .thenThrow(new RuntimeException("db down"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> roleService.findByStatus(true, 0, 10));
        assertEquals("db down", ex.getMessage());
    }

    @Test
    void listRoles_returnsMappedPage() {
        RoleEntity role = RoleEntity.builder()
                .guid(UUID.randomUUID())
                .name("TEACHER")
                .isActive(true)
                .build();
        Page<RoleEntity> page = new PageImpl<>(List.of(role), PageRequest.of(0, 10), 1);
        when(roleRepository.findAllByIsActiveTrue(any(Pageable.class))).thenReturn(page);

        PaginatedResponse<RoleListDTO> response = roleService.listRoles(0, 10);

        assertEquals(1, response.getContent().size());
        assertEquals("TEACHER", response.getContent().get(0).getName());
    }
}
