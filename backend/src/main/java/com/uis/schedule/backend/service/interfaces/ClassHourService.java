package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for class hour management operations.
 */
public interface ClassHourService {

    PaginatedResponse<ClassHourListDTO> listClassHours(int page, int size);

    PaginatedResponse<ClassHourListDTO> listAllClassHoursIncludingInactive(int page, int size);

    PaginatedResponse<ClassHourListDTO> findByStatus(boolean status, int page, int size);

    Optional<ClassHourDetailDTO> findClassHourById(UUID id);

    PaginatedResponse<ClassHourListDTO> findByGroupId(UUID groupId, int page, int size);

    PaginatedResponse<ClassHourListDTO> findByClassroomId(UUID classroomId, int page, int size);

    PaginatedResponse<ClassHourListDTO> findByDayId(UUID dayId, int page, int size);

    PaginatedResponse<ClassHourListDTO> findByTeacherId(UUID teacherId, int page, int size);

    ClassHourResponse createClassHour(CreateClassHourRequest request);

    ClassHourResponse updateClassHour(UUID id, UpdateClassHourRequest request);

    void deleteClassHour(UUID id);
}
