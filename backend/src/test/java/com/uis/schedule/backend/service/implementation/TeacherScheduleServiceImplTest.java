package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.TeacherRepository;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleDTO;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleEntryDTO;
import com.uis.schedule.backend.service.exception.TeacherNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherScheduleServiceImplTest {

    private static final String TEACHER_EMAIL = "docente@uis.edu.co";

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ClassHourRepository classHourRepository;

    private TeacherScheduleServiceImpl service;

    private UUID teacherId;
    private TeacherEntity teacher;

    @BeforeEach
    void setUp() {
        service = new TeacherScheduleServiceImpl(teacherRepository, classHourRepository);
        teacherId = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .userId(UUID.randomUUID())
                .email(TEACHER_EMAIL)
                .firstName("Ana")
                .lastName("Pérez")
                .build();
        teacher = TeacherEntity.builder()
                .teacherId(teacherId)
                .department("Ingeniería de Sistemas")
                .userId(user)
                .build();
    }

    @Test
    void getScheduleByTeacherId_returnsEntriesSortedByDayAndStartTime() {
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(classHourRepository.findScheduleEntriesByTeacherId(teacherId)).thenReturn(List.of(
                entry("viernes", "08:00"),
                entry("lunes", "10:00"),
                entry("miércoles", "07:00"),
                entry("lunes", "08:00")));

        TeacherScheduleDTO result = service.getScheduleByTeacherId(teacherId, admin());

        assertEquals(teacherId, result.getTeacherId());
        assertEquals("Ana Pérez", result.getTeacherName());
        assertEquals("Ingeniería de Sistemas", result.getDepartment());
        List<String> order = result.getEntries().stream()
                .map(e -> e.getDayName() + " " + e.getStartTime())
                .toList();
        assertEquals(List.of("lunes 08:00", "lunes 10:00", "miércoles 07:00", "viernes 08:00"), order);
    }

    @Test
    void getScheduleByTeacherId_teacherWithoutAssignmentsReturnsEmptyList() {
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(classHourRepository.findScheduleEntriesByTeacherId(teacherId)).thenReturn(List.of());

        TeacherScheduleDTO result = service.getScheduleByTeacherId(teacherId, admin());

        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    void getScheduleByTeacherId_unknownTeacherThrowsNotFound() {
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        UserDetails caller = admin();
        assertThrows(TeacherNotFoundException.class, () -> service.getScheduleByTeacherId(teacherId, caller));
        verify(classHourRepository, never()).findScheduleEntriesByTeacherId(any());
    }

    @Test
    void getScheduleByTeacherId_nullIdThrowsIllegalArgument() {
        UserDetails caller = admin();
        assertThrows(IllegalArgumentException.class, () -> service.getScheduleByTeacherId(null, caller));
    }

    @Test
    void getScheduleByTeacherId_teacherCanReadOwnSchedule() {
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(classHourRepository.findScheduleEntriesByTeacherId(teacherId)).thenReturn(List.of(entry("lunes", "08:00")));

        TeacherScheduleDTO result = service.getScheduleByTeacherId(teacherId, teacherUser("DOCENTE@uis.edu.co"));

        assertEquals(1, result.getEntries().size());
    }

    @Test
    void getScheduleByTeacherId_teacherCannotReadAnotherTeacherSchedule() {
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        UserDetails caller = teacherUser("otro@uis.edu.co");
        assertThrows(AccessDeniedException.class, () -> service.getScheduleByTeacherId(teacherId, caller));
        verify(classHourRepository, never()).findScheduleEntriesByTeacherId(any());
    }

    @Test
    void getScheduleForCurrentTeacher_resolvesProfileByEmail() {
        when(teacherRepository.findByUserId_Email(TEACHER_EMAIL)).thenReturn(Optional.of(teacher));
        when(classHourRepository.findScheduleEntriesByTeacherId(teacherId)).thenReturn(List.of(
                entry("martes", "14:00"), entry("lunes", "08:00")));

        TeacherScheduleDTO result = service.getScheduleForCurrentTeacher(TEACHER_EMAIL);

        assertEquals(2, result.getEntries().size());
        assertEquals("lunes", result.getEntries().get(0).getDayName());
    }

    @Test
    void getScheduleForCurrentTeacher_userWithoutProfileThrowsNotFound() {
        when(teacherRepository.findByUserId_Email(TEACHER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(TeacherNotFoundException.class, () -> service.getScheduleForCurrentTeacher(TEACHER_EMAIL));
    }

    private static TeacherScheduleEntryDTO entry(String day, String start) {
        LocalTime startTime = LocalTime.parse(start);
        return TeacherScheduleEntryDTO.builder()
                .classHourId(UUID.randomUUID())
                .dayName(day)
                .startTime(startTime)
                .endTime(startTime.plusHours(2))
                .groupId(UUID.randomUUID())
                .groupName("A1")
                .subjectCode("22966")
                .subjectName("Bases de Datos")
                .build();
    }

    private static UserDetails admin() {
        return User.withUsername("admin@uis.edu.co").password("x").roles("ADMINISTRATOR").build();
    }

    private static UserDetails teacherUser(String email) {
        return User.withUsername(email).password("x").roles("TEACHER").build();
    }
}
