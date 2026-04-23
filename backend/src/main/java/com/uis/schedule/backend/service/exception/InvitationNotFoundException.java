package com.uis.schedule.backend.service.exception;

public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException(String message) {
        super(message);
    }
}