package com.uis.schedule.backend.presentation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ClassroomDTO {
    private Long classroomId;
	private int number;
	private int maxCapacity;
	private String building;
	private String campus;
	private String type;
}
