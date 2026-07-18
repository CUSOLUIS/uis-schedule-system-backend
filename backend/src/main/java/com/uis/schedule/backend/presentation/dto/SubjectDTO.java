package com.uis.schedule.backend.presentation.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SubjectDTO {
    private UUID subjectId;
	private String code;
	private String name;
	private int credits;
	private int theory_hours;
	private int practice_hours;
	private SchoolDTO school;
}
