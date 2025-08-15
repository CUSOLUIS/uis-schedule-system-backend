package com.uis.schedule.backend.util.mapper;

import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.presentation.dto.UserDTO;

public class UserMapper {
	public static UserDTO entityToDTO(UserEntity entity){
		UserDTO dto = new UserDTO();
		dto.setId(entity.getUserId());
		dto.setName(entity.getName());
		dto.setEmail(entity.getEmail());
		return dto;
	}
}
