package com.uis.schedule.backend.service.interfaces;

import java.util.UUID;

import com.uis.schedule.backend.presentation.dto.CreateTeacherRequest;
import com.uis.schedule.backend.presentation.dto.PaginatedResponse;
import com.uis.schedule.backend.presentation.dto.TeacherDTO;
import com.uis.schedule.backend.presentation.dto.UpdateTeacherRequest;

/**
 * Academic teacher catalog (no user accounts / portal).
 */
public interface TeacherService {

    PaginatedResponse<TeacherDTO> listTeachers(int page, int size);

    TeacherDTO findTeacherById(UUID teacherId);

    TeacherDTO createTeacher(CreateTeacherRequest request);

    TeacherDTO updateTeacher(UUID teacherId, UpdateTeacherRequest request);
}
