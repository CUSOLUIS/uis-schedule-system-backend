package com.uis.schedule.backend.presentation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GroupDTO {
	private Long groupId;
	private String name;
	private int capacity;
	private TeacherDTO teacher;
	private AcademicPeriodDTO period;
	private SubjectDTO subject;
}
