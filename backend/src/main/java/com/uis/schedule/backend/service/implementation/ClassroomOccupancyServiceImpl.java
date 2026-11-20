package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.BuildingEntity;
import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.ClassroomRepository;
import com.uis.schedule.backend.presentation.dto.ClassroomOccupancyDTO;
import com.uis.schedule.backend.presentation.dto.ClassroomOccupancyEntryDTO;
import com.uis.schedule.backend.service.exception.ClassroomNotFoundException;
import com.uis.schedule.backend.service.interfaces.ClassroomOccupancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassroomOccupancyServiceImpl implements ClassroomOccupancyService {

    private static final List<String> WEEK_ORDER =
            List.of("lunes", "martes", "miércoles", "miercoles", "jueves", "viernes", "sábado", "sabado", "domingo");

    private static final Comparator<ClassroomOccupancyEntryDTO> OCCUPANCY_ORDER =
            Comparator.comparingInt((ClassroomOccupancyEntryDTO e) -> dayIndex(e.getDayName()))
                    .thenComparing(ClassroomOccupancyEntryDTO::getStartTime,
                            Comparator.nullsLast(Comparator.<LocalTime>naturalOrder()));

    private final ClassroomRepository classroomRepository;
    private final ClassHourRepository classHourRepository;

    @Override
    public ClassroomOccupancyDTO getWeeklyOccupancy(UUID classroomId) {
        if (classroomId == null) {
            throw new IllegalArgumentException("Classroom ID is required");
        }
        ClassroomEntity classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ClassroomNotFoundException(classroomId));

        List<ClassroomOccupancyEntryDTO> entries = classHourRepository
                .findOccupancyEntriesByClassroomId(classroomId)
                .stream()
                .sorted(OCCUPANCY_ORDER)
                .toList();

        BuildingEntity building = classroom.getBuildingId();
        return ClassroomOccupancyDTO.builder()
                .classroomId(classroom.getClassroomId())
                .number(classroom.getNumber())
                .maxCapacity(classroom.getMaxCapacity())
                .buildingCode(building != null ? building.getCode() : null)
                .buildingName(building != null ? building.getName() : classroom.getBuilding())
                .campus(building != null && building.getCampus() != null ? building.getCampus() : classroom.getCampus())
                .entries(entries)
                .build();
    }

    private static int dayIndex(String dayName) {
        if (dayName == null) {
            return WEEK_ORDER.size();
        }
        int index = WEEK_ORDER.indexOf(dayName.toLowerCase(Locale.ROOT));
        return index < 0 ? WEEK_ORDER.size() : index;
    }
}
