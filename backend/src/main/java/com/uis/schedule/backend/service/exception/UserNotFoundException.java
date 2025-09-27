package com.uis.schedule.backend.service.exception;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(){
		super("User not found");
	}

	public UserNotFoundException(Long id){
		super("User with ID "+ id + " not found");
	}
}
