package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.entity.DayWeekEntity;
import com.uis.schedule.backend.persistence.entity.GroupEntity;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.ClassroomRepository;
import com.uis.schedule.backend.persistence.repository.DayWeekRepository;
import com.uis.schedule.backend.persistence.repository.GroupRepository;
import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.ClassHourNotFoundException;
import com.uis.schedule.backend.service.interfaces.ClassHourService;
import com.uis.schedule.backend.util.mapper.ClassHourMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ClassHourService interface.
 */
@Slf4j
@Service
@Transactional
public class ClassHourServiceImpl implements ClassHourService {

    private final ClassHourRepository classHourRepository;
    private final GroupRepository groupRepository;
    private final ClassroomRepository classroomRepository;
    private final DayWeekRepository dayWeekRepository;

    @Autowired
    public ClassHourServiceImpl(ClassHourRepository classHourRepository,
                                GroupRepository groupRepository,
                                ClassroomRepository classroomRepository,
                                DayWeekRepository dayWeekRepository) {
        this.classHourRepository = classHourRepository;
        this.groupRepository = groupRepository;
        this.classroomRepository = classroomRepository;
        this.dayWeekRepository = dayWeekRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> listClassHours(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ClassHourEntity> classHoursPage = classHourRepository.findAllByIsActiveTrue(pageable);
            return convertToPaginatedResponse(classHoursPage);
        } catch (Exception e) {
            log.error("Error retrieving active class hours list", e);
            return new PaginatedResponse<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> listAllClassHoursIncludingInactive(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ClassHourEntity> classHoursPage = classHourRepository.findAll(pageable);
            return convertToPaginatedResponse(classHoursPage);
        } catch (Exception e) {
            log.error("Error retrieving all class hours list", e);
            return new PaginatedResponse<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> findByStatus(boolean status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ClassHourEntity> classHoursPage = classHourRepository.findAllByIsActive(status, pageable);
            return convertToPaginatedResponse(classHoursPage);
        } catch (Exception e) {
            log.error("Error retrieving class hours by status: {}", status, e);
            return new PaginatedResponse<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassHourDetailDTO> findClassHourById(Long id) {
        if (id == null) {
            log.warn("Attempted to find class hour with null ID");
            return Optional.empty();
        }

        return classHourRepository.findById(id)
                .map(ClassHourMapper::entityToDetailDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> findByGroupId(Long groupId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ClassHourEntity> classHoursPage = classHourRepository.findByGroupId_GroupId(groupId, pageable);
            return convertToPaginatedResponse(classHoursPage);
        } catch (Exception e) {
            log.error("Error retrieving class hours by group ID: {}", groupId, e);
            return new PaginatedResponse<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> findByClassroomId(Long classroomId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ClassHourEntity> classHoursPage = classHourRepository.findByClassroomId_ClassroomId(classroomId, pageable);
            return convertToPaginatedResponse(classHoursPage);
        } catch (Exception e) {
            log.error("Error retrieving class hours by classroom ID: {}", classroomId, e);
            return new PaginatedResponse<>();
        }
    }

    private PaginatedResponse<ClassHourListDTO> convertToPaginatedResponse(Page<ClassHourEntity> classHoursPage) {
        List<ClassHourListDTO> content = classHoursPage.getContent().stream()
                .map(ClassHourMapper::entityToListDTO)
                .collect(Collectors.toList());

        return PaginatedResponse.<ClassHourListDTO>builder()
                .content(content)
                .pageNumber(classHoursPage.getNumber())
                .pageSize(classHoursPage.getSize())
                .totalElements(classHoursPage.getTotalElements())
                .totalPages(classHoursPage.getTotalPages())
                .last(classHoursPage.isLast())
                .build();
    }

    @Override
    public ClassHourResponse createClassHour(CreateClassHourRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Create class hour request cannot be null");
        }

        log.info("Creating new class hour for group: {} from {} to {}",
                request.getGroupId(), request.getStartTime(), request.getEndTime());

        GroupEntity group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + request.getGroupId()));

        if (!group.isActive()) {
            throw new IllegalArgumentException("Cannot create a class hour for a disabled group.");
        }

        ClassroomEntity classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found with ID: " + request.getClassroomId()));

        if (!classroom.isActive()) {
            throw new IllegalArgumentException("Cannot create a class hour for a disabled classroom.");
        }

        DayWeekEntity day = dayWeekRepository.findById(request.getDayId())
                .orElseThrow(() -> new IllegalArgumentException("Day of week not found with ID: " + request.getDayId()));

        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateNoOverlap(request.getDayId(), request.getClassroomId(),
                request.getStartTime(), request.getEndTime(), null);

        try {
            ClassHourEntity entity = ClassHourEntity.builder()
                    .groupId(group)
                    .classroomId(classroom)
                    .day(day)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .isActive(true)
                    .build();

            ClassHourEntity savedEntity = classHourRepository.save(entity);
            log.info("Class hour created successfully with ID: {}", savedEntity.getClassHourId());
            return ClassHourMapper.entityToResponse(savedEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating class hour", e);
            throw new IllegalArgumentException("Class hour creation failed: A class hour with the same attributes may already exist.");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating class hour", e);
            throw new RuntimeException("Failed to create class hour", e);
        }
    }

    @Override
    public ClassHourResponse updateClassHour(Long id, UpdateClassHourRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Class hour ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Update class hour request cannot be null");
        }

        log.info("Updating class hour with ID: {}", id);

        ClassHourEntity classHourToUpdate = classHourRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Class hour not found with ID: {}", id);
                    return new ClassHourNotFoundException(id);
                });

        if (request.getGroupId() != null) {
            GroupEntity group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + request.getGroupId()));
            if (!group.isActive()) {
                throw new IllegalArgumentException("Cannot assign a disabled group to a class hour.");
            }
            classHourToUpdate.setGroupId(group);
        }

        if (request.getClassroomId() != null) {
            ClassroomEntity classroom = classroomRepository.findById(request.getClassroomId())
                    .orElseThrow(() -> new IllegalArgumentException("Classroom not found with ID: " + request.getClassroomId()));
            if (!classroom.isActive()) {
                throw new IllegalArgumentException("Cannot assign a disabled classroom to a class hour.");
            }
            classHourToUpdate.setClassroomId(classroom);
        }

        if (request.getDayId() != null) {
            DayWeekEntity day = dayWeekRepository.findById(request.getDayId())
                    .orElseThrow(() -> new IllegalArgumentException("Day of week not found with ID: " + request.getDayId()));
            classHourToUpdate.setDay(day);
        }

        ClassHourMapper.updateEntityFromRequest(classHourToUpdate, request);

        // Validate time range if any time was updated
        LocalTime startTime = request.getStartTime() != null ? request.getStartTime() : classHourToUpdate.getStartTime();
        LocalTime endTime = request.getEndTime() != null ? request.getEndTime() : classHourToUpdate.getEndTime();
        validateTimeRange(startTime, endTime);

        // Validate no overlap if schedule-relevant fields changed
        boolean scheduleChanged = request.getDayId() != null
                || request.getClassroomId() != null
                || request.getStartTime() != null
                || request.getEndTime() != null;

        if (scheduleChanged) {
            Long dayId = classHourToUpdate.getDay() != null ? classHourToUpdate.getDay().getDayId() : null;
            Long classroomId = classHourToUpdate.getClassroomId() != null ? classHourToUpdate.getClassroomId().getClassroomId() : null;
            validateNoOverlap(dayId, classroomId, startTime, endTime, id);
        }

        try {
            ClassHourEntity updatedClassHour = classHourRepository.save(classHourToUpdate);
            log.info("Class hour updated successfully with ID: {}", id);
            return ClassHourMapper.entityToResponse(updatedClassHour);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating class hour: {}", id, e);
            throw new IllegalArgumentException("Class hour update failed: A class hour with the same attributes may already exist.");
        } catch (Exception e) {
            log.error("Unexpected error while updating class hour: {}", id, e);
            throw new RuntimeException("Failed to update class hour", e);
        }
    }

    @Override
    public void deleteClassHour(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Class hour ID cannot be null");
        }

        log.info("Soft deleting class hour with ID: {}", id);

        ClassHourEntity classHour = classHourRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Class hour not found with ID: {}", id);
                    return new ClassHourNotFoundException(id);
                });

        if (!classHour.isActive()) {
            throw new IllegalArgumentException("Class hour is already disabled (soft-deleted)");
        }

        classHour.setActive(false);
        classHourRepository.save(classHour);

        log.info("Class hour soft-deleted successfully with ID: {}", id);
    }

    // ─── Private validation helpers ──────────────────────────────────

    /**
     * Validates that the time range is logically correct:
     * - Both startTime and endTime must not be null
     * - startTime must be strictly before endTime
     */
    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
    }

    /**
     * Validates that no active class hour overlaps with the given schedule
     * on the same day and classroom.
     * <p>
     * Two ranges [A, B) and [C, D) overlap when A &lt; D AND C &lt; B.
     *
     * @param dayId      the day of the week ID
     * @param classroomId the classroom ID
     * @param startTime  the proposed start time
     * @param endTime    the proposed end time
     * @param excludeId  the class hour ID to exclude (null for create)
     */
    private void validateNoOverlap(Long dayId, Long classroomId,
                                   LocalTime startTime, LocalTime endTime,
                                   Long excludeId) {
        List<ClassHourEntity> overlapping;

        if (excludeId != null) {
            overlapping = classHourRepository.findOverlappingHours(
                    dayId, classroomId, startTime, endTime, excludeId);
        } else {
            overlapping = classHourRepository.findOverlappingHoursForCreate(
                    dayId, classroomId, startTime, endTime);
        }

        if (!overlapping.isEmpty()) {
            log.warn("Schedule overlap detected for classroom {} on day {} between {} and {}",
                    classroomId, dayId, startTime, endTime);
            throw new IllegalArgumentException(
                    "The classroom is already occupied on the specified day and time range.");
        }
    }
}
