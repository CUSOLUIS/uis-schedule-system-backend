package com.uis.schedule.backend.service.exception;

/**
 * Access or refresh token that is invalid, revoked, or belongs to a disabled account.
 * Kept apart from {@link InvalidTokenException} so invitation links stay on 400
 * while session failures answer 401.
 */
public class InvalidSessionTokenException extends InvalidTokenException {

    public InvalidSessionTokenException(String message) {
        super(message);
    }
}
