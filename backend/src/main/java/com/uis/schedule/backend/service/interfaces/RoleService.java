package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for role management operations.
 */
public interface RoleService {

    /**
     * Retrieves a list of active roles with pagination.
     *
     * @param page page number (0-indexed)
     * @param size number of items per page
     * @return PaginatedResponse of RoleListDTO
     */
    PaginatedResponse<RoleListDTO> listRoles(int page, int size);

    /**
     * Retrieves all roles (including inactive ones) with pagination.
     *
     * @param page page number (0-indexed)
     * @param size number of items per page
     * @return PaginatedResponse of RoleListDTO
     */
    PaginatedResponse<RoleListDTO> listAllRolesIncludingInactive(int page, int size);

    /**
     * Finds roles by their active status with pagination.
     *
     * @param status the status to filter by
     * @param page   page number (0-indexed)
     * @param size   number of items per page
     * @return PaginatedResponse of RoleListDTO
     */
    PaginatedResponse<RoleListDTO> findByStatus(boolean status, int page, int size);

    /**
     * Finds a role by its ID with complete details.
     *
     * @param id the role ID
     * @return Optional containing RoleDetailDTO if found
     */
    Optional<RoleDetailDTO> findRoleById(UUID id);

    /**
     * Creates a new role.
     *
     * @param request the create role request
     * @return RoleResponse with created role data
     */
    RoleResponse createRole(CreateRoleRequest request);

    /**
     * Updates an existing role.
     *
     * @param id      the role ID
     * @param request the update role request
     * @return RoleResponse with updated role data
     */
    RoleResponse updateRole(UUID id, UpdateRoleRequest request);

    /**
     * Deletes a role by ID (soft delete).
     *
     * @param id the role ID
     */
    void deleteRole(UUID id);
}
