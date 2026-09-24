package com.uis.schedule.backend.service.exception;

public class RoleInUseException extends RuntimeException {

    public RoleInUseException(String message) {
        super(message);
    }
}
