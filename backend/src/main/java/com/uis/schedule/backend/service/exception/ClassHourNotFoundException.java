package com.uis.schedule.backend.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a class hour is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClassHourNotFoundException extends RuntimeException {

    public ClassHourNotFoundException(String message) {
        super(message);
    }

    public ClassHourNotFoundException(Long id) {
        super("Class hour not found with ID: " + id);
    }
}
