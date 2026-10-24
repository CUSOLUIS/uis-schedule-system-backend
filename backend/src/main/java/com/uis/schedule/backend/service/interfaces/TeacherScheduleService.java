package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.TeacherScheduleDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

/**
 * Consulta del horario semanal de un profesor.
 */
public interface TeacherScheduleService {

    /**
     * Horario de un profesor por su id. Un administrador consulta cualquiera;
     * un docente solo el propio.
     */
    TeacherScheduleDTO getScheduleByTeacherId(UUID teacherId, UserDetails caller);

    /**
     * Horario del docente autenticado, resuelto por su email (username del principal).
     */
    TeacherScheduleDTO getScheduleForCurrentTeacher(String email);
}
