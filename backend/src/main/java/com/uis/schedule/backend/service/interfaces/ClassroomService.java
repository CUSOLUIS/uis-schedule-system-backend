package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for classroom management operations.
 */
public interface ClassroomService {

    PaginatedResponse<ClassroomListDTO> listClassrooms(int page, int size);

    PaginatedResponse<ClassroomListDTO> listAllClassroomsIncludingInactive(int page, int size);

    PaginatedResponse<ClassroomListDTO> findByStatus(boolean status, int page, int size);

    PaginatedResponse<ClassroomListDTO> searchClassrooms(String name, String campus, String building, Integer capacity, int page, int size);

    List<ClassroomListDTO> findAvailable(
            UUID dayId,
            LocalTime startTime,
            LocalTime endTime,
            LocalDate startDate,
            LocalDate endDate,
            Integer minCapacity,
            UUID excludeClassHourId);

    Optional<ClassroomDetailDTO> findClassroomById(UUID id);

    ClassroomResponse createClassroom(CreateClassroomRequest request);

    ClassroomResponse updateClassroom(UUID id, UpdateClassroomRequest request);

    void deleteClassroom(UUID id);
}
