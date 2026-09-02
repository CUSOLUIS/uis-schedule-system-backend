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
import com.uis.schedule.backend.service.exception.ScheduleConflictException;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassHourEntity> classHoursPage = classHourRepository.findAllByIsActiveTrueAndGroupId_IsActiveTrue(pageable);
        return convertToPaginatedResponse(classHoursPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> listAllClassHoursIncludingInactive(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassHourEntity> classHoursPage = classHourRepository.findAll(pageable);
        return convertToPaginatedResponse(classHoursPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> findByStatus(boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassHourEntity> classHoursPage = status
                ? classHourRepository.findAllByIsActiveTrueAndGroupId_IsActiveTrue(pageable)
                : classHourRepository.findAllByIsActive(false, pageable);
        return convertToPaginatedResponse(classHoursPage);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassHourDetailDTO> findClassHourById(UUID id) {
        if (id == null) {
            log.warn("Attempted to find class hour with null ID");
            return Optional.empty();
        }

        return classHourRepository.findById(id)
                .filter(ch -> ch.getGroupId() == null || ch.getGroupId().isActive())
                .map(ClassHourMapper::entityToDetailDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> findByGroupId(UUID groupId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassHourEntity> classHoursPage = classHourRepository
                .findByGroupId_GroupIdAndIsActiveTrueAndGroupId_IsActiveTrue(groupId, pageable);
        return convertToPaginatedResponse(classHoursPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> findByClassroomId(UUID classroomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassHourEntity> classHoursPage = classHourRepository
                .findByClassroomId_ClassroomIdAndIsActiveTrueAndGroupId_IsActiveTrue(classroomId, pageable);
        return convertToPaginatedResponse(classHoursPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> findByDayId(UUID dayId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassHourEntity> classHoursPage = classHourRepository
                .findByDays_DayIdAndIsActiveTrueAndGroupId_IsActiveTrue(dayId, pageable);
        return convertToPaginatedResponse(classHoursPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ClassHourListDTO> findByTeacherId(UUID teacherId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassHourEntity> classHoursPage = classHourRepository
                .findByGroupId_TeacherId_TeacherIdAndIsActiveTrueAndGroupId_IsActiveTrue(teacherId, pageable);
        return convertToPaginatedResponse(classHoursPage);
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

        log.info("Creating new class hour for group: {} from {} to {} on days: {}",
                request.getGroupId(), request.getStartTime(), request.getEndTime(), request.getDayIds());

        // Validate group
        GroupEntity group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + request.getGroupId()));

        if (!group.isActive()) {
            throw new IllegalArgumentException("Cannot create a class hour for a disabled group.");
        }

        // Validate classroom
        ClassroomEntity classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found with ID: " + request.getClassroomId()));

        if (!classroom.isActive()) {
            throw new IllegalArgumentException("Cannot create a class hour for a disabled classroom.");
        }

        // Validate days
        List<UUID> dayIds = request.getDayIds();
        if (dayIds == null || dayIds.isEmpty()) {
            throw new IllegalArgumentException("At least one day of the week is required.");
        }

        Set<DayWeekEntity> days = validateAndResolveDays(dayIds);

        // Validate time range
        validateTimeRange(request.getStartTime(), request.getEndTime());

        // Validate academic period dates (required by @NotNull in DTO)
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        validateDateRange(startDate, endDate);

        UUID teacherId = group.getTeacherId() != null ? group.getTeacherId().getTeacherId() : null;
        validateNoOverlap(dayIds, request.getClassroomId(), teacherId,
                request.getStartTime(), request.getEndTime(), startDate, endDate, null);

        try {
            ClassHourEntity entity = ClassHourEntity.builder()
                    .groupId(group)
                    .classroomId(classroom)
                    .days(days)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .startDate(startDate)
                    .endDate(endDate)
                    .isActive(true)
                    .build();

            ClassHourEntity savedEntity = classHourRepository.save(entity);
            log.info("Class hour created successfully with ID: {}", savedEntity.getClassHourId());
            return ClassHourMapper.entityToResponse(savedEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating class hour", e);
            throw new IllegalArgumentException("Class hour creation failed: A class hour with the same attributes may already exist.");
        }
    }

    @Override
    public ClassHourResponse updateClassHour(UUID id, UpdateClassHourRequest request) {
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

        // Update group if provided
        if (request.getGroupId() != null) {
            GroupEntity group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + request.getGroupId()));
            if (!group.isActive()) {
                throw new IllegalArgumentException("Cannot assign a disabled group to a class hour.");
            }
            classHourToUpdate.setGroupId(group);
        }

        // Update classroom if provided
        if (request.getClassroomId() != null) {
            ClassroomEntity classroom = classroomRepository.findById(request.getClassroomId())
                    .orElseThrow(() -> new IllegalArgumentException("Classroom not found with ID: " + request.getClassroomId()));
            if (!classroom.isActive()) {
                throw new IllegalArgumentException("Cannot assign a disabled classroom to a class hour.");
            }
            classHourToUpdate.setClassroomId(classroom);
        }

        // Update days if provided
        if (request.getDayIds() != null) {
            if (request.getDayIds().isEmpty()) {
                throw new IllegalArgumentException("At least one day of the week is required.");
            }
            Set<DayWeekEntity> days = validateAndResolveDays(request.getDayIds());
            classHourToUpdate.setDays(days);
        }

        // Update scalar fields (time, dates) via mapper
        ClassHourMapper.updateEntityFromRequest(classHourToUpdate, request);

        // Validate time range
        LocalTime startTime = classHourToUpdate.getStartTime();
        LocalTime endTime = classHourToUpdate.getEndTime();
        validateTimeRange(startTime, endTime);

        // Validate date range
        validateDateRange(classHourToUpdate.getStartDate(), classHourToUpdate.getEndDate());

        // Validate no overlap if schedule-relevant fields changed
        boolean scheduleChanged = request.getDayIds() != null
                || request.getClassroomId() != null
                || request.getGroupId() != null
                || request.getStartTime() != null
                || request.getEndTime() != null
                || request.getStartDate() != null
                || request.getEndDate() != null;

        if (scheduleChanged) {
            List<UUID> currentDayIds = classHourToUpdate.getDays().stream()
                    .map(DayWeekEntity::getDayId)
                    .collect(Collectors.toList());
            UUID classroomId = classHourToUpdate.getClassroomId() != null ? classHourToUpdate.getClassroomId().getClassroomId() : null;
            UUID teacherId = classHourToUpdate.getGroupId() != null && classHourToUpdate.getGroupId().getTeacherId() != null
                    ? classHourToUpdate.getGroupId().getTeacherId().getTeacherId()
                    : null;
            validateNoOverlap(currentDayIds, classroomId, teacherId, startTime, endTime,
                    classHourToUpdate.getStartDate(), classHourToUpdate.getEndDate(), id);
        }

        try {
            ClassHourEntity updatedClassHour = classHourRepository.save(classHourToUpdate);
            log.info("Class hour updated successfully with ID: {}", id);
            return ClassHourMapper.entityToResponse(updatedClassHour);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating class hour: {}", id, e);
            throw new IllegalArgumentException("Class hour update failed: A class hour with the same attributes may already exist.");
        }
    }

    @Override
    public void deleteClassHour(UUID id) {
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

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
    }

    private Set<DayWeekEntity> validateAndResolveDays(List<UUID> dayIds) {
        if (dayIds.size() != new HashSet<>(dayIds).size()) {
            throw new IllegalArgumentException("Duplicate day IDs are not allowed.");
        }

        Set<DayWeekEntity> days = new HashSet<>();
        for (UUID dayId : dayIds) {
            DayWeekEntity day = dayWeekRepository.findById(dayId)
                    .orElseThrow(() -> new IllegalArgumentException("Day of week not found with ID: " + dayId));
            days.add(day);
        }
        return days;
    }

    private void validateNoOverlap(List<UUID> dayIds, UUID classroomId, UUID teacherId,
                                   LocalTime startTime, LocalTime endTime,
                                   LocalDate startDate, LocalDate endDate,
                                   UUID excludeId) {
        List<ClassHourEntity> overlappingClassroom;

        if (classroomId != null) {
            if (excludeId != null) {
                overlappingClassroom = classHourRepository.findOverlappingHours(
                        dayIds, classroomId, startTime, endTime, startDate, endDate, excludeId);
            } else {
                overlappingClassroom = classHourRepository.findOverlappingHoursForCreate(
                        dayIds, classroomId, startTime, endTime, startDate, endDate);
            }

            if (!overlappingClassroom.isEmpty()) {
                log.warn("Schedule overlap detected for classroom {} on days {} between {} and {} ({} to {})",
                        classroomId, dayIds, startTime, endTime, startDate, endDate);
                throw new ScheduleConflictException(
                        "The classroom is already occupied on the specified days and time range.");
            }
        }

        if (teacherId != null) {
            List<ClassHourEntity> overlappingTeacher = classHourRepository.findOverlappingHoursByTeacher(
                    dayIds, teacherId, startTime, endTime, startDate, endDate, excludeId);
            if (!overlappingTeacher.isEmpty()) {
                log.warn("Schedule overlap detected for teacher {} on days {} between {} and {} ({} to {})",
                        teacherId, dayIds, startTime, endTime, startDate, endDate);
                throw new ScheduleConflictException(
                        "The teacher already has a class hour on the specified days and time range.");
            }
        }
    }
}
