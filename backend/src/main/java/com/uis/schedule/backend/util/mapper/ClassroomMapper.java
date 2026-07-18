package com.uis.schedule.backend.util.mapper;

import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.presentation.dto.*;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping between Classroom entities and DTOs.
 */
@Component
public class ClassroomMapper {

    private ClassroomMapper() {
        // Utility class should not be instantiated
    }

    public static ClassroomListDTO entityToListDTO(ClassroomEntity entity) {
        if (entity == null) {
            return null;
        }

        return ClassroomListDTO.builder()
                .id(entity.getClassroomId())
                .number(entity.getNumber())
                .maxCapacity(entity.getMaxCapacity())
                .building(entity.getBuilding())
                .campus(entity.getCampus())
                .type(entity.getType())
                .isActive(entity.isActive())
                .build();
    }

    public static ClassroomDetailDTO entityToDetailDTO(ClassroomEntity entity) {
        if (entity == null) {
            return null;
        }

        return ClassroomDetailDTO.builder()
                .id(entity.getClassroomId())
                .number(entity.getNumber())
                .maxCapacity(entity.getMaxCapacity())
                .building(entity.getBuilding())
                .campus(entity.getCampus())
                .type(entity.getType())
                .isActive(entity.isActive())
                .build();
    }

    public static ClassroomResponse entityToResponse(ClassroomEntity entity) {
        if (entity == null) {
            return null;
        }

        return ClassroomResponse.builder()
                .id(entity.getClassroomId())
                .number(entity.getNumber())
                .maxCapacity(entity.getMaxCapacity())
                .building(entity.getBuilding())
                .campus(entity.getCampus())
                .type(entity.getType())
                .isActive(entity.isActive())
                .build();
    }

    public static void updateEntityFromRequest(ClassroomEntity entity, UpdateClassroomRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getNumber() != null && !request.getNumber().trim().isEmpty()) {
            entity.setNumber(request.getNumber());
        }

        if (request.getMaxCapacity() != null) {
            entity.setMaxCapacity(request.getMaxCapacity());
        }

        if (request.getBuilding() != null) {
            entity.setBuilding(request.getBuilding());
        }

        if (request.getCampus() != null) {
            entity.setCampus(request.getCampus());
        }

        if (request.getType() != null) {
            entity.setType(request.getType());
        }

        if (request.getIsActive() != null) {
            entity.setActive(request.getIsActive());
        }
    }
}
