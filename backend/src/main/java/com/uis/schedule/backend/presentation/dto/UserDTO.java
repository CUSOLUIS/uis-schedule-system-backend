package com.uis.schedule.backend.presentation.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.*;

@Getter
@Setter
public class UserDTO {
	private Long id;
	private String name;
	private String email;
	private String password;
	private String role;
	private String permissions;
	private boolean active;
	private LocalDateTime lastSession;

	public String getLastSession(){
		return lastSession.toString();
	}

	public void setLastSession(String lastSession){
		this.lastSession = LocalDateTime.parse(lastSession, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}
