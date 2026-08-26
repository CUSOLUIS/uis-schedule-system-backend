package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.entity.GroupEntity;
import com.uis.schedule.backend.persistence.repository.ClassroomRepository;
import com.uis.schedule.backend.persistence.repository.GroupRepository;
import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.CapacityConstraintException;
import com.uis.schedule.backend.service.exception.ClassroomAlreadyExistsException;
import com.uis.schedule.backend.service.exception.ClassroomHasActiveGroupsException;
import com.uis.schedule.backend.service.exception.ClassroomNotFoundException;
import com.uis.schedule.backend.service.interfaces.ClassroomService;
import com.uis.schedule.backend.util.mapper.ClassroomMapper;
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
 * Implementation of ClassroomService interface.
 */
@Slf4j
@Service
@Transactional
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public ClassroomServiceImpl(ClassroomRepository classroomRepository, GroupRepository groupRepository) {
        this.classroomRepository = classroomRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassroomListDTO> listClassrooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassroomEntity> classroomsPage = classroomRepository.findAllByIsActiveTrue(pageable);
        return convertToPaginatedResponse(classroomsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassroomListDTO> listAllClassroomsIncludingInactive(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassroomEntity> classroomsPage = classroomRepository.findAll(pageable);
        return convertToPaginatedResponse(classroomsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassroomListDTO> findByStatus(boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassroomEntity> classroomsPage = classroomRepository.findAllByIsActive(status, pageable);
        return convertToPaginatedResponse(classroomsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassroomListDTO> searchClassrooms(String name, String building, Integer capacity,
                                                                int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassroomEntity> classroomsPage = classroomRepository.searchActive(name, building, capacity, pageable);
        return convertToPaginatedResponse(classroomsPage);
    }

    private PaginatedResponse<ClassroomListDTO> convertToPaginatedResponse(Page<ClassroomEntity> classroomsPage) {
        List<ClassroomListDTO> content = classroomsPage.getContent().stream()
                .map(ClassroomMapper::entityToListDTO)
                .collect(Collectors.toList());

        return PaginatedResponse.<ClassroomListDTO>builder()
                .content(content)
                .pageNumber(classroomsPage.getNumber())
                .pageSize(classroomsPage.getSize())
                .totalElements(classroomsPage.getTotalElements())
                .totalPages(classroomsPage.getTotalPages())
                .last(classroomsPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassroomDetailDTO> findClassroomById(UUID id) {
        if (id == null) {
            log.warn("Attempted to find classroom with null ID");
            return Optional.empty();
        }

        return classroomRepository.findById(id)
                .map(ClassroomMapper::entityToDetailDTO);
    }

    @Override
    public ClassroomResponse createClassroom(CreateClassroomRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Create classroom request cannot be null");
        }

        log.info("Creating new classroom with number: {}", request.getNumber());

        ensureUniqueClassroom(request.getNumber(), request.getCampus(), request.getBuilding(), null);

        try {
            ClassroomEntity entity = ClassroomEntity.builder()
                    .number(request.getNumber())
                    .maxCapacity(request.getMaxCapacity())
                    .building(request.getBuilding())
                    .campus(request.getCampus())
                    .type(request.getType())
                    .isActive(true)
                    .build();

            ClassroomEntity savedEntity = classroomRepository.save(entity);
            log.info("Classroom created successfully with ID: {}", savedEntity.getClassroomId());
            return ClassroomMapper.entityToResponse(savedEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating classroom: {}", request.getNumber(), e);
            throw new ClassroomAlreadyExistsException(
                    "A classroom with the same number, campus and building already exists.");
        }
    }

    @Override
    public ClassroomResponse updateClassroom(UUID id, UpdateClassroomRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Classroom ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Update classroom request cannot be null");
        }

        log.info("Updating classroom with ID: {}", id);

        ClassroomEntity classroomToUpdate = classroomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Classroom not found with ID: {}", id);
                    return new ClassroomNotFoundException(id);
                });

        if (Boolean.FALSE.equals(request.getIsActive()) && classroomToUpdate.isActive()) {
            assertNoActiveGroups(id);
        }

        if (request.getMaxCapacity() != null) {
            validateCapacityAgainstAssignedGroups(id, request.getMaxCapacity());
        }

        String number = request.getNumber() != null ? request.getNumber() : classroomToUpdate.getNumber();
        String campus = request.getCampus() != null ? request.getCampus() : classroomToUpdate.getCampus();
        String building = request.getBuilding() != null ? request.getBuilding() : classroomToUpdate.getBuilding();
        ensureUniqueClassroom(number, campus, building, id);

        try {
            ClassroomMapper.updateEntityFromRequest(classroomToUpdate, request);
            ClassroomEntity updatedClassroom = classroomRepository.save(classroomToUpdate);

            log.info("Classroom updated successfully with ID: {}", id);
            return ClassroomMapper.entityToResponse(updatedClassroom);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating classroom: {}", id, e);
            throw new ClassroomAlreadyExistsException(
                    "A classroom with the same number, campus and building already exists.");
        }
    }

    @Override
    public void deleteClassroom(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Classroom ID cannot be null");
        }

        log.info("Soft deleting classroom with ID: {}", id);

        ClassroomEntity classroom = classroomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Classroom not found with ID: {}", id);
                    return new ClassroomNotFoundException(id);
                });

        if (!classroom.isActive()) {
            throw new IllegalArgumentException("Classroom is already disabled (soft-deleted)");
        }

        assertNoActiveGroups(id);

        classroom.setActive(false);
        classroomRepository.save(classroom);

        log.info("Classroom soft-deleted successfully with ID: {}", id);
    }

    private void assertNoActiveGroups(UUID classroomId) {
        if (groupRepository.existsByClassroomId_ClassroomIdAndIsActiveTrue(classroomId)) {
            throw new ClassroomHasActiveGroupsException(
                    "Cannot deactivate or delete a classroom that still has active groups assigned.");
        }
    }

    private void validateCapacityAgainstAssignedGroups(UUID classroomId, int newMaxCapacity) {
        List<GroupEntity> assignedGroups = groupRepository.findAllByClassroomId_ClassroomIdAndIsActiveTrue(classroomId);
        int highestGroupCapacity = assignedGroups.stream()
                .map(GroupEntity::getCapacity)
                .filter(capacity -> capacity != null)
                .max(Integer::compareTo)
                .orElse(0);

        if (newMaxCapacity < highestGroupCapacity) {
            throw new CapacityConstraintException(
                    "Classroom capacity (" + newMaxCapacity
                            + ") cannot be lower than the largest assigned group capacity ("
                            + highestGroupCapacity + ").");
        }
    }

    private void ensureUniqueClassroom(String number, String campus, String building, UUID excludeId) {
        if (number == null) {
            return;
        }
        if (classroomRepository.existsActiveDuplicate(number, campus, building, excludeId)) {
            throw new ClassroomAlreadyExistsException(
                    "A classroom with the same number, campus and building already exists.");
        }
    }
}
