package com.uis.schedule.backend.service.exception;

public class InvitationConflictException extends RuntimeException {
    public InvitationConflictException(String message) {
        super(message);
    }
}
