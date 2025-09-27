package com.uis.schedule.backend.util.mapper;

import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.presentation.dto.UserDTO;

public class UserMapper {
	public static UserDTO entityToDTO(UserEntity entity){
		UserDTO dto = new UserDTO();
		dto.setId(entity.getUserId());
		dto.setName(entity.getName());
		dto.setEmail(entity.getEmail());
		dto.setPassword(entity.getPassword());
		dto.setRole(entity.getRole());
		dto.setPermissions(entity.getPermissions());
		dto.setActive(entity.isActive());
		dto.setLastSession(entity.getLastSession());
		return dto;
	}

	public static UserEntity dtoToEntity(UserDTO dto){
		UserEntity entity = new UserEntity();
		entity.setUserId(dto.getId());
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setPassword(dto.getPassword());
		entity.setRole(dto.getRole());
		entity.setPermissions(dto.getPermissions());
		entity.setActive(dto.isActive());
		entity.setLastSession(dto.getLastSession());
		return entity;
	}
}
