package com.uis.schedule.backend.presentation.dto;

import java.time.LocalTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ClassHourDTO {
    private Long classHourId;
	private DayWeekDTO day;
	private LocalTime hour;
	private GroupDTO group;
	private ClassroomDTO classroom;
}
