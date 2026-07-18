package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleDTO {
    private UUID scheduleId;
	private ClassDTO classId;
	private UserListDTO user;
}
