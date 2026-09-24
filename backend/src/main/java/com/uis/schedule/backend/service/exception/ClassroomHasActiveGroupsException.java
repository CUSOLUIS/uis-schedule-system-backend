package com.uis.schedule.backend.service.exception;

public class ClassroomHasActiveGroupsException extends RuntimeException {

    public ClassroomHasActiveGroupsException(String message) {
        super(message);
    }
}
