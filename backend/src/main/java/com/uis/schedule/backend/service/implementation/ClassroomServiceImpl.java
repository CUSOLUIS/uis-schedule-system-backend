package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.repository.ClassroomRepository;
import com.uis.schedule.backend.presentation.dto.*;
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

    @Autowired
    public ClassroomServiceImpl(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
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
            throw new IllegalArgumentException("Classroom creation failed: The classroom '" + request.getNumber() + "' may already exist.");
        } catch (Exception e) {
            log.error("Unexpected error while creating classroom: {}", request.getNumber(), e);
            throw new RuntimeException("Failed to create classroom", e);
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

        try {
            ClassroomMapper.updateEntityFromRequest(classroomToUpdate, request);
            ClassroomEntity updatedClassroom = classroomRepository.save(classroomToUpdate);

            log.info("Classroom updated successfully with ID: {}", id);
            return ClassroomMapper.entityToResponse(updatedClassroom);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating classroom: {}", id, e);
            throw new IllegalArgumentException("Classroom update failed: A classroom with the same attributes may already exist.");
        } catch (Exception e) {
            log.error("Unexpected error while updating classroom: {}", id, e);
            throw new RuntimeException("Failed to update classroom", e);
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

        classroom.setActive(false);
        classroomRepository.save(classroom);

        log.info("Classroom soft-deleted successfully with ID: {}", id);
    }
}
