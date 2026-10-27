package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.configuration.JacksonConfig;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleDTO;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleEntryDTO;
import com.uis.schedule.backend.presentation.handler.GlobalExceptionHandler;
import com.uis.schedule.backend.service.exception.TeacherNotFoundException;
import com.uis.schedule.backend.service.interfaces.TeacherScheduleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contrato HTTP del controlador (envoltorio Data/Message/Errors y códigos de estado).
 * Los @PreAuthorize no se evalúan en modo standalone; la regla de propiedad vive en el servicio.
 */
@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    private static final String TEACHER_EMAIL = "docente@uis.edu.co";

    @Mock
    private TeacherScheduleService teacherScheduleService;

    private MockMvc mockMvc;
    private UserDetails principal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TeacherController(teacherScheduleService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                // Mismo ObjectMapper de la aplicación: horas como "HH:mm:ss", no como arreglo.
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new JacksonConfig().objectMapper()))
                .build();
        principal = User.withUsername(TEACHER_EMAIL).password("x").roles("TEACHER").build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void mySchedule_returnsEntriesOnSeveralDays() throws Exception {
        when(teacherScheduleService.getScheduleForCurrentTeacher(TEACHER_EMAIL)).thenReturn(schedule(List.of(
                entry("lunes", "08:00", "Bases de Datos"),
                entry("miércoles", "10:00", "Estructuras de Datos"))));

        mockMvc.perform(get("/api/teachers/me/schedule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Message").value(TeacherController.SCHEDULE_RETRIEVED))
                .andExpect(jsonPath("$.Errors").isEmpty())
                .andExpect(jsonPath("$.Data.teacherName").value("Ana Pérez"))
                .andExpect(jsonPath("$.Data.entries.length()").value(2))
                .andExpect(jsonPath("$.Data.entries[0].dayName").value("lunes"))
                .andExpect(jsonPath("$.Data.entries[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$.Data.entries[1].dayName").value("miércoles"))
                .andExpect(jsonPath("$.Data.entries[1].subjectName").value("Estructuras de Datos"))
                .andExpect(jsonPath("$.Data.entries[1].classroomNumber").value("301"));
    }

    @Test
    void mySchedule_withoutAssignmentsReturnsEmptyListAndInformativeMessage() throws Exception {
        when(teacherScheduleService.getScheduleForCurrentTeacher(TEACHER_EMAIL)).thenReturn(schedule(List.of()));

        mockMvc.perform(get("/api/teachers/me/schedule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Message").value(TeacherController.SCHEDULE_EMPTY))
                .andExpect(jsonPath("$.Data.entries").isEmpty());
    }

    @Test
    void mySchedule_userWithoutTeacherProfileReturns404() throws Exception {
        when(teacherScheduleService.getScheduleForCurrentTeacher(TEACHER_EMAIL))
                .thenThrow(new TeacherNotFoundException("The authenticated user is not linked to a teacher profile."));

        mockMvc.perform(get("/api/teachers/me/schedule"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Data").doesNotExist())
                .andExpect(jsonPath("$.Message").value("The authenticated user is not linked to a teacher profile."));
    }

    @Test
    void scheduleByTeacher_returnsSchedule() throws Exception {
        UUID teacherId = UUID.randomUUID();
        when(teacherScheduleService.getScheduleByTeacherId(eq(teacherId), any()))
                .thenReturn(schedule(List.of(entry("viernes", "14:00", "Redes"))));

        mockMvc.perform(get("/api/teachers/{teacherId}/schedule", teacherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Data.entries[0].dayName").value("viernes"));
    }

    @Test
    void scheduleByTeacher_unknownTeacherReturns404() throws Exception {
        UUID teacherId = UUID.randomUUID();
        when(teacherScheduleService.getScheduleByTeacherId(eq(teacherId), any()))
                .thenThrow(new TeacherNotFoundException(teacherId));

        mockMvc.perform(get("/api/teachers/{teacherId}/schedule", teacherId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Message").value("Teacher not found with ID: " + teacherId))
                .andExpect(jsonPath("$.Errors[0]").value("Teacher not found with ID: " + teacherId));
    }

    @Test
    void scheduleByTeacher_otherTeacherReturns403() throws Exception {
        UUID teacherId = UUID.randomUUID();
        when(teacherScheduleService.getScheduleByTeacherId(eq(teacherId), any()))
                .thenThrow(new AccessDeniedException("Teachers can only view their own schedule"));

        mockMvc.perform(get("/api/teachers/{teacherId}/schedule", teacherId))
                .andExpect(status().isForbidden());
    }

    @Test
    void scheduleByTeacher_invalidUuidReturns400() throws Exception {
        mockMvc.perform(get("/api/teachers/{teacherId}/schedule", "no-es-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Message").value("Invalid value for parameter 'teacherId'"));
        verifyNoInteractions(teacherScheduleService);
    }

    private static TeacherScheduleDTO schedule(List<TeacherScheduleEntryDTO> entries) {
        return TeacherScheduleDTO.builder()
                .teacherId(UUID.randomUUID())
                .teacherName("Ana Pérez")
                .department("Ingeniería de Sistemas")
                .entries(entries)
                .build();
    }

    private static TeacherScheduleEntryDTO entry(String day, String start, String subject) {
        LocalTime startTime = LocalTime.parse(start);
        return TeacherScheduleEntryDTO.builder()
                .classHourId(UUID.randomUUID())
                .dayName(day)
                .startTime(startTime)
                .endTime(startTime.plusHours(2))
                .groupId(UUID.randomUUID())
                .groupName("A1")
                .groupCapacity(35)
                .subjectCode("22966")
                .subjectName(subject)
                .subjectCredits(4)
                .classroomNumber("301")
                .building("Edificio de Ingeniería de Sistemas")
                .build();
    }
}
