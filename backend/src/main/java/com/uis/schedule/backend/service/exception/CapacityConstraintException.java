package com.uis.schedule.backend.service.exception;

public class CapacityConstraintException extends RuntimeException {

    public CapacityConstraintException(String message) {
        super(message);
    }
}
