package com.uis.schedule.backend.presentation.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
public class UserDTO {
	private UUID id;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String password;
	private boolean active;
	private LocalDateTime lastSession;

	public String getLastSession(){
		return lastSession != null ? lastSession.toString() : null;
	}

	public void setLastSession(String lastSession){
		this.lastSession = LocalDateTime.parse(lastSession, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}
