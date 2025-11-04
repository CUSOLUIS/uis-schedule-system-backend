package com.uis.schedule.backend.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RequestResponseUtils {
    private RequestResponseUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus status) {
        return new ResponseEntity<String>("Message: " + message, status);
    }
}
