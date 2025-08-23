package com.uis.schedule.backend.presentation.dto;

import java.time.LocalDateTime;

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
}
