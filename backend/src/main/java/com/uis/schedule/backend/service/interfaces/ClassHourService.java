package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.*;

import java.util.Optional;

/**
 * Service interface for class hour management operations.
 */
public interface ClassHourService {

    /**
     * Retrieves a list of active class hours with pagination.
     *
     * @param page page number (0-indexed)
     * @param size number of items per page
     * @return PaginatedResponse of ClassHourListDTO
     */
    PaginatedResponse<ClassHourListDTO> listClassHours(int page, int size);

    /**
     * Retrieves all class hours (including inactive ones) with pagination.
     *
     * @param page page number (0-indexed)
     * @param size number of items per page
     * @return PaginatedResponse of ClassHourListDTO
     */
    PaginatedResponse<ClassHourListDTO> listAllClassHoursIncludingInactive(int page, int size);

    /**
     * Finds class hours by their active status with pagination.
     *
     * @param status the status to filter by
     * @param page   page number (0-indexed)
     * @param size   number of items per page
     * @return PaginatedResponse of ClassHourListDTO
     */
    PaginatedResponse<ClassHourListDTO> findByStatus(boolean status, int page, int size);

    /**
     * Finds a class hour by its ID with complete details.
     *
     * @param id the class hour ID
     * @return Optional containing ClassHourDetailDTO if found
     */
    Optional<ClassHourDetailDTO> findClassHourById(Long id);

    /**
     * Finds class hours by group ID with pagination.
     *
     * @param groupId the group ID
     * @param page    page number (0-indexed)
     * @param size    number of items per page
     * @return PaginatedResponse of ClassHourListDTO
     */
    PaginatedResponse<ClassHourListDTO> findByGroupId(Long groupId, int page, int size);

    /**
     * Finds class hours by classroom ID with pagination.
     *
     * @param classroomId the classroom ID
     * @param page        page number (0-indexed)
     * @param size        number of items per page
     * @return PaginatedResponse of ClassHourListDTO
     */
    PaginatedResponse<ClassHourListDTO> findByClassroomId(Long classroomId, int page, int size);

    /**
     * Creates a new class hour.
     *
     * @param request the create class hour request
     * @return ClassHourResponse with created class hour data
     */
    ClassHourResponse createClassHour(CreateClassHourRequest request);

    /**
     * Updates an existing class hour.
     *
     * @param id      the class hour ID
     * @param request the update class hour request
     * @return ClassHourResponse with updated class hour data
     */
    ClassHourResponse updateClassHour(Long id, UpdateClassHourRequest request);

    /**
     * Deletes a class hour by ID (soft delete).
     *
     * @param id the class hour ID
     */
    void deleteClassHour(Long id);
}
