package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import com.uis.schedule.backend.persistence.entity.DayWeekEntity;
import com.uis.schedule.backend.persistence.entity.GroupEntity;
import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.repository.AcademicPeriodRepository;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.ClassroomRepository;
import com.uis.schedule.backend.persistence.repository.GroupRepository;
import com.uis.schedule.backend.persistence.repository.SubjectRepository;
import com.uis.schedule.backend.persistence.repository.TeacherRepository;
import com.uis.schedule.backend.presentation.dto.UpdateGroupRequest;
import com.uis.schedule.backend.service.exception.ScheduleConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private AcademicPeriodRepository academicPeriodRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private ClassHourRepository classHourRepository;

    private GroupServiceImpl groupService;

    @BeforeEach
    void setUp() {
        groupService = new GroupServiceImpl(
                groupRepository,
                classroomRepository,
                academicPeriodRepository,
                subjectRepository,
                teacherRepository,
                classHourRepository);
    }

    @Test
    void updateGroup_whenTeacherChanges_checksOverlappingHours() {
        UUID groupId = UUID.randomUUID();
        UUID currentTeacherId = UUID.randomUUID();
        UUID newTeacherId = UUID.randomUUID();
        UUID dayId = UUID.randomUUID();
        UUID hourId = UUID.randomUUID();
        UUID otherGroupId = UUID.randomUUID();

        GroupEntity group = GroupEntity.builder()
                .groupId(groupId)
                .name("A")
                .capacity(30)
                .isActive(true)
                .teacherId(TeacherEntity.builder().teacherId(currentTeacherId).build())
                .build();

        ClassHourEntity hour = ClassHourEntity.builder()
                .classHourId(hourId)
                .groupId(group)
                .days(Set.of(DayWeekEntity.builder().dayId(dayId).name("LUNES").build()))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .startDate(LocalDate.of(2026, 7, 16))
                .endDate(LocalDate.of(2026, 11, 20))
                .isActive(true)
                .build();

        ClassHourEntity conflict = ClassHourEntity.builder()
                .classHourId(UUID.randomUUID())
                .groupId(GroupEntity.builder().groupId(otherGroupId).build())
                .build();

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(teacherRepository.existsById(newTeacherId)).thenReturn(true);
        when(classHourRepository.findAllByGroupId_GroupIdAndIsActiveTrue(groupId)).thenReturn(List.of(hour));
        when(classHourRepository.findOverlappingHoursByTeacher(
                eq(List.of(dayId)),
                eq(newTeacherId),
                eq(hour.getStartTime()),
                eq(hour.getEndTime()),
                eq(hour.getStartDate()),
                eq(hour.getEndDate()),
                eq(hourId))).thenReturn(List.of(conflict));

        UpdateGroupRequest request = UpdateGroupRequest.builder().teacherId(newTeacherId).build();

        assertThrows(ScheduleConflictException.class, () -> groupService.updateGroup(groupId, request));
        verify(classHourRepository).findOverlappingHoursByTeacher(
                any(), eq(newTeacherId), any(), any(), any(), any(), eq(hourId));
    }
}
