package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.BuildingEntity;
import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.ClassroomRepository;
import com.uis.schedule.backend.presentation.dto.ClassroomOccupancyEntryDTO;
import com.uis.schedule.backend.service.exception.ClassroomNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassroomOccupancyServiceImplTest {

    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private ClassHourRepository classHourRepository;

    private ClassroomOccupancyServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClassroomOccupancyServiceImpl(classroomRepository, classHourRepository);
    }

    @Test
    void getWeeklyOccupancy_returnsBuildingMetadataAndSortedEntries() {
        UUID classroomId = UUID.randomUUID();
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(ClassroomEntity.builder()
                .classroomId(classroomId)
                .number("201")
                .maxCapacity(40)
                .buildingId(BuildingEntity.builder().code("CH2").name("Humanas 2").campus("UIS").build())
                .isActive(true)
                .build()));
        when(classHourRepository.findOccupancyEntriesByClassroomId(classroomId)).thenReturn(List.of(
                ClassroomOccupancyEntryDTO.builder()
                        .classHourId(UUID.randomUUID())
                        .dayName("viernes")
                        .startTime(LocalTime.of(14, 0))
                        .build(),
                ClassroomOccupancyEntryDTO.builder()
                        .classHourId(UUID.randomUUID())
                        .dayName("lunes")
                        .startTime(LocalTime.of(8, 0))
                        .build()
        ));

        var occupancy = service.getWeeklyOccupancy(classroomId);

        assertEquals("CH2", occupancy.getBuildingCode());
        assertEquals("Humanas 2", occupancy.getBuildingName());
        assertEquals("lunes", occupancy.getEntries().get(0).getDayName());
        assertEquals("viernes", occupancy.getEntries().get(1).getDayName());
    }

    @Test
    void getWeeklyOccupancy_missingClassroom_throws() {
        UUID classroomId = UUID.randomUUID();
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.empty());
        assertThrows(ClassroomNotFoundException.class, () -> service.getWeeklyOccupancy(classroomId));
    }
}
