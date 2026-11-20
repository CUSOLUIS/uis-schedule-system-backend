package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.ClassroomOccupancyDTO;

import java.util.UUID;

public interface ClassroomOccupancyService {

    ClassroomOccupancyDTO getWeeklyOccupancy(UUID classroomId);
}
