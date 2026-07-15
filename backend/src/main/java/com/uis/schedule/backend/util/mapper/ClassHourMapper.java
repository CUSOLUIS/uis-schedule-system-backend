package com.uis.schedule.backend.util.mapper;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.entity.DayWeekEntity;
import com.uis.schedule.backend.persistence.entity.GroupEntity;
import com.uis.schedule.backend.presentation.dto.*;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping between ClassHour entities and DTOs.
 */
@Component
public class ClassHourMapper {

    private ClassHourMapper() {
        // Utility class should not be instantiated
    }

    public static ClassHourListDTO entityToListDTO(ClassHourEntity entity) {
        if (entity == null) {
            return null;
        }

        return ClassHourListDTO.builder()
                .id(entity.getClassHourId())
                .hour(entity.getHour())
                .groupId(entity.getGroupId() != null ? entity.getGroupId().getGroupId() : null)
                .classroomId(entity.getClassroomId() != null ? entity.getClassroomId().getClassroomId() : null)
                .dayId(entity.getDay() != null ? entity.getDay().getDayId() : null)
                .isActive(entity.isActive())
                .build();
    }

    public static ClassHourDetailDTO entityToDetailDTO(ClassHourEntity entity) {
        if (entity == null) {
            return null;
        }

        return ClassHourDetailDTO.builder()
                .id(entity.getClassHourId())
                .hour(entity.getHour())
                .groupId(entity.getGroupId() != null ? entity.getGroupId().getGroupId() : null)
                .classroomId(entity.getClassroomId() != null ? entity.getClassroomId().getClassroomId() : null)
                .dayId(entity.getDay() != null ? entity.getDay().getDayId() : null)
                .isActive(entity.isActive())
                .build();
    }

    public static ClassHourResponse entityToResponse(ClassHourEntity entity) {
        if (entity == null) {
            return null;
        }

        return ClassHourResponse.builder()
                .id(entity.getClassHourId())
                .hour(entity.getHour())
                .groupId(entity.getGroupId() != null ? entity.getGroupId().getGroupId() : null)
                .classroomId(entity.getClassroomId() != null ? entity.getClassroomId().getClassroomId() : null)
                .dayId(entity.getDay() != null ? entity.getDay().getDayId() : null)
                .isActive(entity.isActive())
                .build();
    }

    public static void updateEntityFromRequest(ClassHourEntity entity, UpdateClassHourRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getHour() != null) {
            entity.setHour(request.getHour());
        }
    }
}
