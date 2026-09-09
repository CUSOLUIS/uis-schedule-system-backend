package com.uis.schedule.backend.service.exception;

public class ClassroomAlreadyExistsException extends RuntimeException {

    public ClassroomAlreadyExistsException(String message) {
        super(message);
    }
}
