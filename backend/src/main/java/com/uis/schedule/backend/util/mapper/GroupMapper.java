package com.uis.schedule.backend.util.mapper;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.entity.GroupEntity;
import com.uis.schedule.backend.presentation.dto.*;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping between Group entities and DTOs.
 */
@Component
public class GroupMapper {

    private GroupMapper() {
        // Utility class should not be instantiated
    }

    public static GroupListDTO entityToListDTO(GroupEntity entity) {
        if (entity == null) {
            return null;
        }

        return GroupListDTO.builder()
                .id(entity.getGroupId())
                .name(entity.getName())
                .capacity(entity.getCapacity())
                .classroomId(entity.getClassroomId() != null ? entity.getClassroomId().getClassroomId() : null)
                .teacherId(entity.getTeacherId() != null ? entity.getTeacherId().getTeacherId() : null)
                .periodId(entity.getPeriodId() != null ? entity.getPeriodId().getPeriodId() : null)
                .subjectId(entity.getSubjectId() != null ? entity.getSubjectId().getSubjectId() : null)
                .isActive(entity.isActive())
                .build();
    }

    public static GroupDetailDTO entityToDetailDTO(GroupEntity entity) {
        if (entity == null) {
            return null;
        }

        return GroupDetailDTO.builder()
                .id(entity.getGroupId())
                .name(entity.getName())
                .capacity(entity.getCapacity())
                .classroomId(entity.getClassroomId() != null ? entity.getClassroomId().getClassroomId() : null)
                .teacherId(entity.getTeacherId() != null ? entity.getTeacherId().getTeacherId() : null)
                .periodId(entity.getPeriodId() != null ? entity.getPeriodId().getPeriodId() : null)
                .subjectId(entity.getSubjectId() != null ? entity.getSubjectId().getSubjectId() : null)
                .isActive(entity.isActive())
                .build();
    }

    public static GroupResponse entityToResponse(GroupEntity entity) {
        if (entity == null) {
            return null;
        }

        return GroupResponse.builder()
                .id(entity.getGroupId())
                .name(entity.getName())
                .capacity(entity.getCapacity())
                .classroomId(entity.getClassroomId() != null ? entity.getClassroomId().getClassroomId() : null)
                .teacherId(entity.getTeacherId() != null ? entity.getTeacherId().getTeacherId() : null)
                .periodId(entity.getPeriodId() != null ? entity.getPeriodId().getPeriodId() : null)
                .subjectId(entity.getSubjectId() != null ? entity.getSubjectId().getSubjectId() : null)
                .isActive(entity.isActive())
                .build();
    }

    public static void updateEntityFromRequest(GroupEntity entity, UpdateGroupRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setName(request.getName());
        }

        if (request.getCapacity() != null) {
            entity.setCapacity(request.getCapacity());
        }
    }
}
