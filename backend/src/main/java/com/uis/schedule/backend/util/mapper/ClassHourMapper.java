package com.uis.schedule.backend.util.mapper;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import com.uis.schedule.backend.presentation.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for mapping between ClassHour entities and DTOs.
 */
@Component
public class ClassHourMapper {

    private ClassHourMapper() {
        // Utility class should not be instantiated
    }

    /**
     * Extracts a sorted list of day names (lowercase) from the entity's days set.
     * The names are sorted alphabetically for consistent ordering.
     */
    private static List<String> extractDayNames(ClassHourEntity entity) {
        if (entity.getDays() == null) {
            return new ArrayList<>();
        }
        return entity.getDays().stream()
                .map(day -> day.getName() != null ? day.getName().toLowerCase() : null)
                .sorted()
                .collect(Collectors.toList());
    }

    public static ClassHourListDTO entityToListDTO(ClassHourEntity entity) {
        if (entity == null) {
            return null;
        }

        return ClassHourListDTO.builder()
                .id(entity.getClassHourId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .groupId(entity.getGroupId() != null ? entity.getGroupId().getGroupId() : null)
                .classroomId(entity.getClassroomId() != null ? entity.getClassroomId().getClassroomId() : null)
                .dayNames(extractDayNames(entity))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.isActive())
                .build();
    }

    public static ClassHourDetailDTO entityToDetailDTO(ClassHourEntity entity) {
        if (entity == null) {
            return null;
        }

        return ClassHourDetailDTO.builder()
                .id(entity.getClassHourId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .groupId(entity.getGroupId() != null ? entity.getGroupId().getGroupId() : null)
                .classroomId(entity.getClassroomId() != null ? entity.getClassroomId().getClassroomId() : null)
                .dayNames(extractDayNames(entity))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.isActive())
                .build();
    }

    public static ClassHourResponse entityToResponse(ClassHourEntity entity) {
        if (entity == null) {
            return null;
        }

        return ClassHourResponse.builder()
                .id(entity.getClassHourId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .groupId(entity.getGroupId() != null ? entity.getGroupId().getGroupId() : null)
                .classroomId(entity.getClassroomId() != null ? entity.getClassroomId().getClassroomId() : null)
                .dayNames(extractDayNames(entity))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.isActive())
                .build();
    }

    public static void updateEntityFromRequest(ClassHourEntity entity, UpdateClassHourRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getStartTime() != null) {
            entity.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            entity.setEndTime(request.getEndTime());
        }
        if (request.getStartDate() != null) {
            entity.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            entity.setEndDate(request.getEndDate());
        }
    }
}
