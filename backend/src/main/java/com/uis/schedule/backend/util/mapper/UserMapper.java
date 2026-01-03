package com.uis.schedule.backend.util.mapper;

import com.uis.schedule.backend.persistence.entity.PermissionEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.presentation.dto.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between User entities and DTOs.
 * Follows the Single Responsibility Principle by handling only mapping logic.
 */
public class UserMapper {

	/**
	 * Converts UserEntity to UserListDTO for list views.
	 * 
	 * @param entity the user entity
	 * @return UserListDTO with minimal information
	 */
	public static UserListDTO entityToListDTO(UserEntity entity) {
		if (entity == null) {
			return null;
		}

		return UserListDTO.builder()
				.id(entity.getUserId())
				.name(entity.getName())
				.email(entity.getEmail())
				.active(entity.isEnable())
				.build();
	}

	/**
	 * Converts UserEntity to UserDetailDTO for detailed views.
	 * 
	 * @param entity the user entity
	 * @return UserDetailDTO with complete information
	 */
	public static UserDetailDTO entityToDetailDTO(UserEntity entity) {
		if (entity == null) {
			return null;
		}

		Set<String> roleNames = entity.getRoles() != null
				? entity.getRoles().stream()
						.map(role -> role.getRoleEnum().name())
						.collect(Collectors.toSet())
				: Collections.emptySet();

		Set<String> permissionNames = entity.getRoles() != null
				? entity.getRoles().stream()
						.flatMap(role -> role.getPermissionList().stream())
						.map(PermissionEntity::getName)
						.collect(Collectors.toSet())
				: Collections.emptySet();

		return UserDetailDTO.builder()
				.id(entity.getUserId())
				.name(entity.getName())
				.email(entity.getEmail())
				.roles(roleNames)
				.permissions(permissionNames)
				.active(entity.isEnable())
				.accountNoExpired(entity.isAccountNoExpired())
				.accountNoLocked(entity.isAccountNoLocked())
				.credentialNoExpired(entity.isCredentialNoExpired())
				.lastSession(entity.getLastSessionDateTime())
				.build();
	}

	/**
	 * Converts UserEntity to UserResponse for API responses.
	 * 
	 * @param entity the user entity
	 * @return UserResponse without sensitive data
	 */
	public static UserResponse entityToResponse(UserEntity entity) {
		if (entity == null) {
			return null;
		}

		return UserResponse.builder()
				.id(entity.getUserId())
				.name(entity.getName())
				.email(entity.getEmail())
				.active(entity.isEnable())
				.lastSession(entity.getLastSessionDateTime())
				.build();
	}

	/**
	 * Updates an existing UserEntity with data from UpdateUserRequest.
	 * Only updates non-null fields from the request.
	 * 
	 * @param entity  the entity to update
	 * @param request the update request
	 */
	public static void updateEntityFromRequest(UserEntity entity, UpdateUserRequest request) {
		if (entity == null || request == null) {
			return;
		}

		if (request.getName() != null) {
			entity.setName(request.getName());
		}
		if (request.getEmail() != null) {
			entity.setEmail(request.getEmail());
		}
		if (request.getActive() != null) {
			entity.setEnable(request.getActive());
		}
	}

	// Legacy methods for backward compatibility with existing code

	/**
	 * @deprecated Use entityToDetailDTO instead
	 */
	@Deprecated
	public static UserDTO entityToDTO(UserEntity entity) {
		if (entity == null) {
			return null;
		}

		UserDTO dto = new UserDTO();
		dto.setId(entity.getUserId());
		dto.setName(entity.getName());
		dto.setEmail(entity.getEmail());
		dto.setPassword(entity.getPassword());
		dto.setPermissions(entity.getPermissions());
		dto.setActive(entity.isEnable());
		dto.setLastSession(entity.getLastSession());
		return dto;
	}

	/**
	 * @deprecated Use createRequestToEntity instead
	 */
	@Deprecated
	public static UserEntity dtoToEntity(UserDTO dto) {
		if (dto == null) {
			return null;
		}

		UserEntity entity = new UserEntity();
		entity.setUserId(dto.getId());
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setPassword(dto.getPassword());
		entity.setPermissions(dto.getPermissions());
		entity.setEnable(dto.isActive());
		if (dto.getLastSession() != null) {
			entity.setLastSession(dto.getLastSession());
		}
		return entity;
	}
}
