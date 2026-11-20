package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.TeacherScheduleDTO;

import java.util.UUID;

/**
 * Weekly schedule queries for catalog teachers (no portal /me).
 */
public interface TeacherScheduleService {

    TeacherScheduleDTO getScheduleByTeacherId(UUID teacherId);
}
