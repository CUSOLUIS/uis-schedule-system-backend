package com.uis.schedule.backend.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a classroom is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClassroomNotFoundException extends RuntimeException {

    public ClassroomNotFoundException(String message) {
        super(message);
    }

    public ClassroomNotFoundException(Long id) {
        super("Classroom not found with ID: " + id);
    }
}
