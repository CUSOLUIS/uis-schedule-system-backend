package com.uis.schedule.backend.presentation.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FacultyDTO {
    private UUID id;
	private String name;
}
