package com.uis.schedule.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClassDTO {
	private Long classId;
	private String classType;
	private UserListDTO user;
	private GroupDTO group;
}
