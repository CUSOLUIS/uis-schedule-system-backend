package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.repository.RoleRepository;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.RoleNotFoundException;
import com.uis.schedule.backend.service.interfaces.RoleService;
import com.uis.schedule.backend.util.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of RoleService interface.
 */
@Slf4j
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private static final String ADMINISTRATOR_ROLE = "ADMINISTRATOR";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RoleListDTO> listRoles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleEntity> rolesPage = roleRepository.findAllByIsActiveTrue(pageable);
        return convertToPaginatedResponse(rolesPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RoleListDTO> listAllRolesIncludingInactive(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleEntity> rolesPage = roleRepository.findAll(pageable);
        return convertToPaginatedResponse(rolesPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RoleListDTO> findByStatus(boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleEntity> rolesPage = roleRepository.findAllByIsActive(status, pageable);
        return convertToPaginatedResponse(rolesPage);
    }

    private PaginatedResponse<RoleListDTO> convertToPaginatedResponse(Page<RoleEntity> rolesPage) {
        List<RoleListDTO> content = rolesPage.getContent().stream()
                .map(RoleMapper::entityToListDTO)
                .collect(Collectors.toList());

        return PaginatedResponse.<RoleListDTO>builder()
                .content(content)
                .pageNumber(rolesPage.getNumber())
                .pageSize(rolesPage.getSize())
                .totalElements(rolesPage.getTotalElements())
                .totalPages(rolesPage.getTotalPages())
                .last(rolesPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleDetailDTO> findRoleById(UUID id) {
        if (id == null) {
            log.warn("Attempted to find role with null ID");
            return Optional.empty();
        }

        return roleRepository.findById(id)
                .map(RoleMapper::entityToDetailDTO);
    }

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Create role request cannot be null");
        }

        log.info("Creating new role with name: {}", request.getName());

        try {
            RoleEntity entity = RoleEntity.builder()
                    .name(request.getName().toUpperCase())
                    .isActive(request.getIsActive())
                    .build();

            RoleEntity savedEntity = roleRepository.save(entity);
            log.info("Role created successfully with ID: {}", savedEntity.getGuid());
            return RoleMapper.entityToResponse(savedEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating role: {}", request.getName(), e);
            throw new IllegalArgumentException("Role creation failed: The role '" + request.getName() + "' may already exist.");
        } catch (Exception e) {
            log.error("Unexpected error while creating role: {}", request.getName(), e);
            throw new RuntimeException("Failed to create role", e);
        }
    }

    @Override
    public RoleResponse updateRole(UUID id, UpdateRoleRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Update role request cannot be null");
        }

        log.info("Updating role with ID: {}", id);

        RoleEntity roleToUpdate = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new RoleNotFoundException("Role not found with id: " + id);
                });

        assertAdministratorNotMutated(roleToUpdate);

        if (request.getName() != null) {
            request.setName(request.getName().toUpperCase());
        }

        try {
            RoleMapper.updateEntityFromRequest(roleToUpdate, request);
            RoleEntity updatedRole = roleRepository.save(roleToUpdate);

            log.info("Role updated successfully with ID: {}", id);
            return RoleMapper.entityToResponse(updatedRole);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating role: {}", id, e);
            throw new IllegalArgumentException("Role name '" + request.getName() + "' is already in use.");
        } catch (Exception e) {
            log.error("Unexpected error while updating role: {}", id, e);
            throw new RuntimeException("Failed to update role", e);
        }
    }

    @Override
    public void deleteRole(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }

        log.info("Soft deleting role with ID: {}", id);

        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new RoleNotFoundException("Role not found with id: " + id);
                });

        if (!role.getIsActive()) {
            throw new IllegalArgumentException("Role is already disabled (soft-deleted)");
        }

        if (ADMINISTRATOR_ROLE.equalsIgnoreCase(role.getName())) {
            log.warn("Attempt to delete ADMINISTRATOR role blocked for ID: {}", id);
            throw new IllegalStateException("Security protection: The ADMINISTRATOR role cannot be deleted.");
        }

        if (userRepository.existsByRoles_Guid(id)) {
            log.warn("Attempt to delete role assigned to users blocked for ID: {}", id);
            throw new IllegalStateException("Role is assigned to one or more users and cannot be deleted.");
        }

        role.setIsActive(false);
        roleRepository.save(role);

        log.info("Role soft-deleted successfully with ID: {}", id);
    }

    private void assertAdministratorNotMutated(RoleEntity role) {
        if (ADMINISTRATOR_ROLE.equalsIgnoreCase(role.getName())) {
            log.warn("Attempt to modify or deactivate ADMINISTRATOR role blocked for ID: {}", role.getGuid());
            throw new IllegalStateException(
                    "Security protection: The ADMINISTRATOR role cannot be modified or deactivated.");
        }
    }
}
