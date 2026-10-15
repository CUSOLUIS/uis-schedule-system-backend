package com.uis.schedule.backend.service.exception;

/**
 * Thrown when the authenticated account has no teacher profile.
 */
public class TeacherNotFoundException extends RuntimeException {

    public TeacherNotFoundException(String message) {
        super(message);
    }
}
