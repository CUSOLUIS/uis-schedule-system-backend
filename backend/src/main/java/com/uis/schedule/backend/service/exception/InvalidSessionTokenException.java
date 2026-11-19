package com.uis.schedule.backend.service.exception;

/**
 * Token de sesión (access o refresh) inválido, revocado o de una cuenta
 * deshabilitada. Se separa de {@link InvalidTokenException} para responder 401
 * sin cambiar el 400 de los enlaces de invitación.
 */
public class InvalidSessionTokenException extends InvalidTokenException {
    public InvalidSessionTokenException(String message) {
        super(message);
    }
}
