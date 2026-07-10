package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.*;

import java.util.Optional;

/**
 * Service interface for classroom management operations.
 */
public interface ClassroomService {

    /**
     * Retrieves a list of active classrooms with pagination.
     *
     * @param page page number (0-indexed)
     * @param size number of items per page
     * @return PaginatedResponse of ClassroomListDTO
     */
    PaginatedResponse<ClassroomListDTO> listClassrooms(int page, int size);

    /**
     * Retrieves all classrooms (including inactive ones) with pagination.
     *
     * @param page page number (0-indexed)
     * @param size number of items per page
     * @return PaginatedResponse of ClassroomListDTO
     */
    PaginatedResponse<ClassroomListDTO> listAllClassroomsIncludingInactive(int page, int size);

    /**
     * Finds classrooms by their active status with pagination.
     *
     * @param status the status to filter by
     * @param page   page number (0-indexed)
     * @param size   number of items per page
     * @return PaginatedResponse of ClassroomListDTO
     */
    PaginatedResponse<ClassroomListDTO> findByStatus(boolean status, int page, int size);

    /**
     * Finds a classroom by its ID with complete details.
     *
     * @param id the classroom ID
     * @return Optional containing ClassroomDetailDTO if found
     */
    Optional<ClassroomDetailDTO> findClassroomById(Long id);

    /**
     * Creates a new classroom.
     *
     * @param request the create classroom request
     * @return ClassroomResponse with created classroom data
     */
    ClassroomResponse createClassroom(CreateClassroomRequest request);

    /**
     * Updates an existing classroom.
     *
     * @param id      the classroom ID
     * @param request the update classroom request
     * @return ClassroomResponse with updated classroom data
     */
    ClassroomResponse updateClassroom(Long id, UpdateClassroomRequest request);

    /**
     * Deletes a classroom by ID (soft delete).
     *
     * @param id the classroom ID
     */
    void deleteClassroom(Long id);
}
