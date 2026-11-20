package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.TeacherRepository;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleEntryDTO;
import com.uis.schedule.backend.service.exception.TeacherNotFoundException;
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
class TeacherScheduleServiceImplTest {

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private ClassHourRepository classHourRepository;

    private TeacherScheduleServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TeacherScheduleServiceImpl(teacherRepository, classHourRepository);
    }

    @Test
    void getScheduleByTeacherId_usesCatalogFullNameAndSortsByDay() {
        UUID teacherId = UUID.randomUUID();
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(TeacherEntity.builder()
                .teacherId(teacherId)
                .fullName("Ana Pérez")
                .linkType("PLANTA")
                .department("Historia")
                .build()));
        when(classHourRepository.findScheduleEntriesByTeacherId(teacherId)).thenReturn(List.of(
                TeacherScheduleEntryDTO.builder()
                        .classHourId(UUID.randomUUID())
                        .dayName("martes")
                        .startTime(LocalTime.of(8, 0))
                        .build(),
                TeacherScheduleEntryDTO.builder()
                        .classHourId(UUID.randomUUID())
                        .dayName("lunes")
                        .startTime(LocalTime.of(10, 0))
                        .build()
        ));

        var schedule = service.getScheduleByTeacherId(teacherId);

        assertEquals("Ana Pérez", schedule.getTeacherName());
        assertEquals("PLANTA", schedule.getLinkType());
        assertEquals("lunes", schedule.getEntries().get(0).getDayName());
        assertEquals("martes", schedule.getEntries().get(1).getDayName());
    }

    @Test
    void getScheduleByTeacherId_missingTeacher_throws404() {
        UUID teacherId = UUID.randomUUID();
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());
        assertThrows(TeacherNotFoundException.class, () -> service.getScheduleByTeacherId(teacherId));
    }
}
