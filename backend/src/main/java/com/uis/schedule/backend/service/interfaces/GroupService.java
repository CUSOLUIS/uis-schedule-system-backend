package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.*;

import java.util.Optional;

/**
 * Service interface for group management operations.
 */
public interface GroupService {

    /**
     * Retrieves a list of active groups with pagination.
     *
     * @param page page number (0-indexed)
     * @param size number of items per page
     * @return PaginatedResponse of GroupListDTO
     */
    PaginatedResponse<GroupListDTO> listGroups(int page, int size);

    /**
     * Retrieves all groups (including inactive ones) with pagination.
     *
     * @param page page number (0-indexed)
     * @param size number of items per page
     * @return PaginatedResponse of GroupListDTO
     */
    PaginatedResponse<GroupListDTO> listAllGroupsIncludingInactive(int page, int size);

    /**
     * Finds groups by their active status with pagination.
     *
     * @param status the status to filter by
     * @param page   page number (0-indexed)
     * @param size   number of items per page
     * @return PaginatedResponse of GroupListDTO
     */
    PaginatedResponse<GroupListDTO> findByStatus(boolean status, int page, int size);

    /**
     * Finds a group by its ID with complete details.
     *
     * @param id the group ID
     * @return Optional containing GroupDetailDTO if found
     */
    Optional<GroupDetailDTO> findGroupById(Long id);

    /**
     * Creates a new group.
     *
     * @param request the create group request
     * @return GroupResponse with created group data
     */
    GroupResponse createGroup(CreateGroupRequest request);

    /**
     * Updates an existing group.
     *
     * @param id      the group ID
     * @param request the update group request
     * @return GroupResponse with updated group data
     */
    GroupResponse updateGroup(Long id, UpdateGroupRequest request);

    /**
     * Deletes a group by ID (soft delete).
     *
     * @param id the group ID
     */
    void deleteGroup(Long id);
}
