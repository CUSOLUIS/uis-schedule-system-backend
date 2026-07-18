package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.*;
import com.uis.schedule.backend.persistence.repository.ClassroomRepository;
import com.uis.schedule.backend.persistence.repository.GroupRepository;
import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.GroupNotFoundException;
import com.uis.schedule.backend.service.interfaces.GroupService;
import com.uis.schedule.backend.util.mapper.GroupMapper;
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
 * Implementation of GroupService interface.
 */
@Slf4j
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final ClassroomRepository classroomRepository;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, ClassroomRepository classroomRepository) {
        this.groupRepository = groupRepository;
        this.classroomRepository = classroomRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<GroupListDTO> listGroups(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupEntity> groupsPage = groupRepository.findAllByIsActiveTrue(pageable);
            return convertToPaginatedResponse(groupsPage);
        } catch (Exception e) {
            log.error("Error retrieving active groups list", e);
            return new PaginatedResponse<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<GroupListDTO> listAllGroupsIncludingInactive(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupEntity> groupsPage = groupRepository.findAll(pageable);
            return convertToPaginatedResponse(groupsPage);
        } catch (Exception e) {
            log.error("Error retrieving all groups list", e);
            return new PaginatedResponse<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<GroupListDTO> findByStatus(boolean status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupEntity> groupsPage = groupRepository.findAllByIsActive(status, pageable);
            return convertToPaginatedResponse(groupsPage);
        } catch (Exception e) {
            log.error("Error retrieving groups by status: {}", status, e);
            return new PaginatedResponse<>();
        }
    }

    private PaginatedResponse<GroupListDTO> convertToPaginatedResponse(Page<GroupEntity> groupsPage) {
        List<GroupListDTO> content = groupsPage.getContent().stream()
                .map(GroupMapper::entityToListDTO)
                .collect(Collectors.toList());

        return PaginatedResponse.<GroupListDTO>builder()
                .content(content)
                .pageNumber(groupsPage.getNumber())
                .pageSize(groupsPage.getSize())
                .totalElements(groupsPage.getTotalElements())
                .totalPages(groupsPage.getTotalPages())
                .last(groupsPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GroupDetailDTO> findGroupById(UUID id) {
        if (id == null) {
            log.warn("Attempted to find group with null ID");
            return Optional.empty();
        }

        return groupRepository.findById(id)
                .map(GroupMapper::entityToDetailDTO);
    }

    @Override
    public GroupResponse createGroup(CreateGroupRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Create group request cannot be null");
        }

        log.info("Creating new group with name: {}", request.getName());

        ClassroomEntity classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found with ID: " + request.getClassroomId()));

        if (!classroom.isActive()) {
            throw new IllegalArgumentException("Cannot assign a disabled classroom to a group.");
        }

        validateCapacity(request.getCapacity(), classroom.getMaxCapacity());

        try {
            GroupEntity entity = GroupEntity.builder()
                    .name(request.getName())
                    .capacity(request.getCapacity())
                    .classroomId(classroom)
                    .isActive(true)
                    .build();

            resolveOptionalRelations(entity, request.getTeacherId(), request.getPeriodId(), request.getSubjectId());

            GroupEntity savedEntity = groupRepository.save(entity);
            log.info("Group created successfully with ID: {}", savedEntity.getGroupId());
            return GroupMapper.entityToResponse(savedEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating group: {}", request.getName(), e);
            throw new IllegalArgumentException("Group creation failed: A group with the same attributes may already exist.");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating group: {}", request.getName(), e);
            throw new RuntimeException("Failed to create group", e);
        }
    }

    @Override
    public GroupResponse updateGroup(UUID id, UpdateGroupRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Update group request cannot be null");
        }

        log.info("Updating group with ID: {}", id);

        GroupEntity groupToUpdate = groupRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Group not found with ID: {}", id);
                    return new GroupNotFoundException(id);
                });

        // If classroom is being changed, validate capacity against the new classroom
        if (request.getClassroomId() != null) {
            ClassroomEntity classroom = classroomRepository.findById(request.getClassroomId())
                    .orElseThrow(() -> new IllegalArgumentException("Classroom not found with ID: " + request.getClassroomId()));

            if (!classroom.isActive()) {
                throw new IllegalArgumentException("Cannot assign a disabled classroom to a group.");
            }

            groupToUpdate.setClassroomId(classroom);

            // Validate capacity against the (possibly new) classroom
            int capacityToValidate = request.getCapacity() != null ? request.getCapacity() : groupToUpdate.getCapacity();
            validateCapacity(capacityToValidate, classroom.getMaxCapacity());
        }

        // If capacity is being changed (and classroom is NOT being changed), validate against existing classroom
        if (request.getCapacity() != null && request.getClassroomId() == null && groupToUpdate.getClassroomId() != null) {
            validateCapacity(request.getCapacity(), groupToUpdate.getClassroomId().getMaxCapacity());
        }

        GroupMapper.updateEntityFromRequest(groupToUpdate, request);

        resolveOptionalRelations(groupToUpdate, request.getTeacherId(), request.getPeriodId(), request.getSubjectId());

        try {
            GroupEntity updatedGroup = groupRepository.save(groupToUpdate);
            log.info("Group updated successfully with ID: {}", id);
            return GroupMapper.entityToResponse(updatedGroup);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating group: {}", id, e);
            throw new IllegalArgumentException("Group update failed: A group with the same attributes may already exist.");
        } catch (Exception e) {
            log.error("Unexpected error while updating group: {}", id, e);
            throw new RuntimeException("Failed to update group", e);
        }
    }

    @Override
    public void deleteGroup(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }

        log.info("Soft deleting group with ID: {}", id);

        GroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Group not found with ID: {}", id);
                    return new GroupNotFoundException(id);
                });

        if (!group.isActive()) {
            throw new IllegalArgumentException("Group is already disabled (soft-deleted)");
        }

        group.setActive(false);
        groupRepository.save(group);

        log.info("Group soft-deleted successfully with ID: {}", id);
    }

    /**
     * Validates that the group capacity does not exceed the classroom max capacity
     * and is a positive value.
     */
    private void validateCapacity(int capacity, int classroomMaxCapacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Group capacity must be greater than zero.");
        }
        if (capacity > classroomMaxCapacity) {
            throw new IllegalArgumentException(
                    "Group capacity (" + capacity + ") cannot exceed classroom max capacity (" + classroomMaxCapacity + ").");
        }
    }

    /**
     * Resolves optional FK relations (teacher, period, subject) from their IDs.
     */
    private void resolveOptionalRelations(GroupEntity entity, UUID teacherId, UUID periodId, UUID subjectId) {
        // Teacher resolution would require TeacherRepository injection — kept simple for now
        // These optional relations can be resolved when their CRUDs are implemented
        // For now, we leave them null if no repository is available
    }
}
