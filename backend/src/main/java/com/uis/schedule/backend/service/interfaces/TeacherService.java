package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.TeacherDTO;

/**
 * Read access to teacher profiles linked to user accounts.
 */
public interface TeacherService {

    /**
     * Returns the teacher profile of the authenticated account.
     * Creates the profile when the account already has the TEACHER role but no row yet.
     *
     * @param usernameOrEmail principal name (email) from the security context
     * @return teacher profile including teacherId
     */
    TeacherDTO getCurrentTeacher(String usernameOrEmail);
}
