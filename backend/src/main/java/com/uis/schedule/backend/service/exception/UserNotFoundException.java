package com.uis.schedule.backend.service.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(){
		super("User not found");
	}

	public UserNotFoundException(String message) {
        super(message);
    }

	public UserNotFoundException(UUID id){
		super("User with ID "+ id + " not found");
	}
}
