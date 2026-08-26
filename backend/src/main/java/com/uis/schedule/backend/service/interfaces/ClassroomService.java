package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for classroom management operations.
 */
public interface ClassroomService {

    PaginatedResponse<ClassroomListDTO> listClassrooms(int page, int size);

    PaginatedResponse<ClassroomListDTO> listAllClassroomsIncludingInactive(int page, int size);

    PaginatedResponse<ClassroomListDTO> findByStatus(boolean status, int page, int size);

    PaginatedResponse<ClassroomListDTO> searchClassrooms(String name, String building, Integer capacity, int page, int size);

    Optional<ClassroomDetailDTO> findClassroomById(UUID id);

    ClassroomResponse createClassroom(CreateClassroomRequest request);

    ClassroomResponse updateClassroom(UUID id, UpdateClassroomRequest request);

    void deleteClassroom(UUID id);
}
