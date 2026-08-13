package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for group management operations.
 */
public interface GroupService {

    PaginatedResponse<GroupListDTO> listGroups(int page, int size);

    PaginatedResponse<GroupListDTO> listAllGroupsIncludingInactive(int page, int size);

    PaginatedResponse<GroupListDTO> findByStatus(boolean status, int page, int size);

    Optional<GroupDetailDTO> findGroupById(UUID id);

    PaginatedResponse<GroupListDTO> searchByName(String name, int page, int size);

    PaginatedResponse<GroupListDTO> findByClassroom(UUID classroomId, int page, int size);

    PaginatedResponse<GroupListDTO> findBySubject(UUID subjectId, int page, int size);

    GroupResponse createGroup(CreateGroupRequest request);

    GroupResponse updateGroup(UUID id, UpdateGroupRequest request);

    void deleteGroup(UUID id);
}
