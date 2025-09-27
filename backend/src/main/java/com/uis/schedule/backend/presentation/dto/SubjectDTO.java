package com.uis.schedule.backend.presentation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class SubjectDTO {
	private Long subjectId;
	private String code;
	private String name;
	private int credits;
	private int theory_hours;
	private int practice_hours;
	private SchoolDTO school;
}
