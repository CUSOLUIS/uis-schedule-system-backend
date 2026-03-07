package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleDTO {
	private Long scheduleId;
	private ClassDTO classId;
	private UserListDTO user;
}
