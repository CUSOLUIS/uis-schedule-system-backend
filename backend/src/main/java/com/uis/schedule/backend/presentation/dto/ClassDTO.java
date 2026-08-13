package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClassDTO {
    private UUID classId;
	private String classType;
	private UserListDTO user;
	private GroupDTO group;
}
