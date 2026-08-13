package com.uis.schedule.backend.presentation.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TeacherDTO {
    private UUID teacherId;
	private String availability;
	private String department;
	private UserListDTO user;
}
