package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.entity.DayWeekEntity;
import com.uis.schedule.backend.persistence.entity.GroupEntity;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.ClassroomRepository;
import com.uis.schedule.backend.persistence.repository.DayWeekRepository;
import com.uis.schedule.backend.persistence.repository.GroupRepository;
import com.uis.schedule.backend.persistence.repository.TeacherRepository;
import com.uis.schedule.backend.presentation.dto.CreateClassHourRequest;
import com.uis.schedule.backend.presentation.dto.UpdateClassHourRequest;
import com.uis.schedule.backend.service.exception.ScheduleConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassHourServiceImplTest {

    @Mock
    private ClassHourRepository classHourRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private DayWeekRepository dayWeekRepository;
    @Mock
    private TeacherRepository teacherRepository;

    private ClassHourServiceImpl classHourService;

    @BeforeEach
    void setUp() {
        classHourService = new ClassHourServiceImpl(
                classHourRepository, groupRepository, classroomRepository, dayWeekRepository, teacherRepository);
    }

    @Test
    void createClassHour_allowsFillerCapacityExceedingClassroom() {
        UUID groupId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();
        UUID dayId = UUID.randomUUID();

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(GroupEntity.builder()
                .groupId(groupId)
                .capacity(100)
                .isActive(true)
                .build()));
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(ClassroomEntity.builder()
                .classroomId(classroomId)
                .number("101")
                .maxCapacity(30)
                .isActive(true)
                .build()));
        when(dayWeekRepository.findById(dayId)).thenReturn(Optional.of(DayWeekEntity.builder()
                .dayId(dayId)
                .name("LUNES")
                .build()));
        when(classHourRepository.findOverlappingHoursByGroupForCreate(anyList(), eq(groupId), any(), any(), any(), any()))
                .thenReturn(List.of());
        when(classHourRepository.findOverlappingHoursForCreate(anyList(), eq(classroomId), any(), any(), any(), any()))
                .thenReturn(List.of());
        when(classHourRepository.save(any(ClassHourEntity.class))).thenAnswer(invocation -> {
            ClassHourEntity entity = invocation.getArgument(0);
            entity.setClassHourId(UUID.randomUUID());
            return entity;
        });

        CreateClassHourRequest request = CreateClassHourRequest.builder()
                .groupId(groupId)
                .classroomId(classroomId)
                .dayIds(List.of(dayId))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .startDate(LocalDate.of(2026, 7, 16))
                .endDate(LocalDate.of(2026, 11, 20))
                .build();

        assertDoesNotThrow(() -> classHourService.createClassHour(request));
    }

    @Test
    void createClassHour_withoutClassroom_setsNoClassroomRequired() {
        UUID groupId = UUID.randomUUID();
        UUID dayId = UUID.randomUUID();

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(GroupEntity.builder()
                .groupId(groupId)
                .capacity(10)
                .isActive(true)
                .build()));
        when(dayWeekRepository.findById(dayId)).thenReturn(Optional.of(DayWeekEntity.builder()
                .dayId(dayId)
                .name("MARTES")
                .build()));
        when(classHourRepository.findOverlappingHoursByGroupForCreate(anyList(), eq(groupId), any(), any(), any(), any()))
                .thenReturn(List.of());
        when(classHourRepository.save(any(ClassHourEntity.class))).thenAnswer(invocation -> {
            ClassHourEntity entity = invocation.getArgument(0);
            entity.setClassHourId(UUID.randomUUID());
            return entity;
        });

        CreateClassHourRequest request = CreateClassHourRequest.builder()
                .groupId(groupId)
                .dayIds(List.of(dayId))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .startDate(LocalDate.of(2026, 7, 16))
                .endDate(LocalDate.of(2026, 11, 20))
                .build();

        var response = classHourService.createClassHour(request);
        assertEquals(ClassHourEntity.STATUS_NO_CLASSROOM_REQUIRED, response.getStatus());
    }

    @Test
    void updateClassHour_reactivating_revalidatesOverlaps() {
        UUID hourId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID dayId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();

        ClassHourEntity existing = ClassHourEntity.builder()
                .classHourId(hourId)
                .groupId(GroupEntity.builder().groupId(groupId).capacity(20).isActive(true).build())
                .classroomId(ClassroomEntity.builder().classroomId(classroomId).maxCapacity(40).isActive(true).build())
                .days(new HashSet<>(Set.of(DayWeekEntity.builder().dayId(dayId).name("MIERCOLES").build())))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .startDate(LocalDate.of(2026, 7, 16))
                .endDate(LocalDate.of(2026, 11, 20))
                .isActive(false)
                .status(ClassHourEntity.STATUS_ASSIGNED)
                .build();

        when(classHourRepository.findById(hourId)).thenReturn(Optional.of(existing));
        when(classHourRepository.findOverlappingHoursByGroup(anyList(), eq(groupId), any(), any(), any(), any(), eq(hourId)))
                .thenReturn(List.of(ClassHourEntity.builder().classHourId(UUID.randomUUID()).build()));

        UpdateClassHourRequest request = UpdateClassHourRequest.builder().isActive(true).build();

        assertThrows(ScheduleConflictException.class, () -> classHourService.updateClassHour(hourId, request));
        verify(classHourRepository).findOverlappingHoursByGroup(anyList(), eq(groupId), any(), any(), any(), any(), eq(hourId));
    }
}
