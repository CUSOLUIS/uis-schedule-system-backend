package com.uis.schedule.backend.presentation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class SchoolDTO {
	private Long id;
	private String name;
	private FacultyDTO faculty;
}
