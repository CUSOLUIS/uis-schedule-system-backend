package com.uis.schedule.backend.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Exception thrown when a teacher profile is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TeacherNotFoundException extends RuntimeException {

    public TeacherNotFoundException(String message) {
        super(message);
    }

    public TeacherNotFoundException(UUID id) {
        super("Teacher not found with ID: " + id);
    }
}
