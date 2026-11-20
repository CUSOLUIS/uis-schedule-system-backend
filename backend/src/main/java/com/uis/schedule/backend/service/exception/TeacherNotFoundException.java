package com.uis.schedule.backend.service.exception;

import java.util.UUID;

/**
 * Thrown when a teacher catalog entry is not found.
 */
public class TeacherNotFoundException extends RuntimeException {

    public TeacherNotFoundException(String message) {
        super(message);
    }

    public TeacherNotFoundException(UUID id) {
        super("Teacher not found with ID: " + id);
    }
}
