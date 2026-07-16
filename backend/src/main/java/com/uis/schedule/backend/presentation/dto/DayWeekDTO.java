package com.uis.schedule.backend.presentation.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DayWeekDTO {
    private UUID dayId;
	private String name;
}
