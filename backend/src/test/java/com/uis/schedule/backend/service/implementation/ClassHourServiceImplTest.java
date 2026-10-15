package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.entity.GroupEntity;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.ClassroomRepository;
import com.uis.schedule.backend.persistence.repository.DayWeekRepository;
import com.uis.schedule.backend.persistence.repository.GroupRepository;
import com.uis.schedule.backend.presentation.dto.CreateClassHourRequest;
import com.uis.schedule.backend.service.exception.CapacityConstraintException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private ClassHourServiceImpl classHourService;

    @BeforeEach
    void setUp() {
        classHourService = new ClassHourServiceImpl(
                classHourRepository, groupRepository, classroomRepository, dayWeekRepository);
    }

    @Test
    void createClassHour_rejectsGroupLargerThanClassroom() {
        UUID groupId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(GroupEntity.builder()
                .groupId(groupId)
                .capacity(40)
                .isActive(true)
                .build()));
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(ClassroomEntity.builder()
                .classroomId(classroomId)
                .number("101")
                .maxCapacity(30)
                .isActive(true)
                .build()));

        CreateClassHourRequest request = CreateClassHourRequest.builder()
                .groupId(groupId)
                .classroomId(classroomId)
                .dayIds(List.of(UUID.randomUUID()))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .startDate(LocalDate.of(2026, 7, 16))
                .endDate(LocalDate.of(2026, 11, 20))
                .build();

        CapacityConstraintException ex = assertThrows(CapacityConstraintException.class,
                () -> classHourService.createClassHour(request));

        assertEquals("Group capacity (40) cannot exceed classroom max capacity (30).", ex.getMessage());
    }
}
