package com.uis.schedule.backend.util.mapper;

import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.presentation.dto.*;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping between Role entities and DTOs.
 */
@Component
public class RoleMapper {

    private RoleMapper() {
        // Utility class should not be instantiated
    }

    public static RoleListDTO entityToListDTO(RoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return RoleListDTO.builder()
                .guid(entity.getGuid())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .build();
    }

    public static RoleDetailDTO entityToDetailDTO(RoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return RoleDetailDTO.builder()
                .guid(entity.getGuid())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static RoleResponse entityToResponse(RoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return RoleResponse.builder()
                .guid(entity.getGuid())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .build();
    }

    public static void updateEntityFromRequest(RoleEntity entity, UpdateRoleRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setName(request.getName());
        }

        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
    }
}
