package com.uis.schedule.backend.presentation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TeacherDTO {
	private Long teacherId;
	private String availability;
	private String department;
	private UserDTO user;
}
