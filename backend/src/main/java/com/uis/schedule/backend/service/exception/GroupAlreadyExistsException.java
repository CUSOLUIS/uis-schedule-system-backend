package com.uis.schedule.backend.service.exception;

public class GroupAlreadyExistsException extends RuntimeException {

    public GroupAlreadyExistsException(String message) {
        super(message);
    }
}
