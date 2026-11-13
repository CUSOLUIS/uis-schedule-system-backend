package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.TeacherRepository;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleDTO;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleEntryDTO;
import com.uis.schedule.backend.service.exception.TeacherNotFoundException;
import com.uis.schedule.backend.service.interfaces.TeacherScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of TeacherScheduleService interface.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class TeacherScheduleServiceImpl implements TeacherScheduleService {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMINISTRATOR";

    // Los ids de day_of_week son UUID: el orden de la semana se fija aquí.
    private static final List<String> WEEK_ORDER =
            List.of("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo");

    private static final Comparator<TeacherScheduleEntryDTO> SCHEDULE_ORDER =
            Comparator.comparingInt((TeacherScheduleEntryDTO e) -> dayIndex(e.getDayName()))
                    .thenComparing(TeacherScheduleEntryDTO::getStartTime,
                            Comparator.nullsLast(Comparator.<LocalTime>naturalOrder()));

    private final TeacherRepository teacherRepository;
    private final ClassHourRepository classHourRepository;

    public TeacherScheduleServiceImpl(TeacherRepository teacherRepository,
                                      ClassHourRepository classHourRepository) {
        this.teacherRepository = teacherRepository;
        this.classHourRepository = classHourRepository;
    }

    @Override
    public TeacherScheduleDTO getScheduleByTeacherId(UUID teacherId, UserDetails caller) {
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID is required");
        }
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException(teacherId));

        if (!isAdministrator(caller) && !isOwner(teacher, caller)) {
            log.warn("Access denied to schedule of teacher {}", teacherId);
            throw new AccessDeniedException("Teachers can only view their own schedule");
        }
        return buildSchedule(teacher);
    }

    @Override
    public TeacherScheduleDTO getScheduleForCurrentTeacher(String email) {
        if (email == null || email.isBlank()) {
            throw new TeacherNotFoundException("The authenticated user is not linked to a teacher profile.");
        }
        TeacherEntity teacher = teacherRepository.findByUserId_Email(email.toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new TeacherNotFoundException(
                        "The authenticated user is not linked to a teacher profile."));
        return buildSchedule(teacher);
    }

    private TeacherScheduleDTO buildSchedule(TeacherEntity teacher) {
        List<TeacherScheduleEntryDTO> entries = classHourRepository
                .findScheduleEntriesByTeacherId(teacher.getTeacherId())
                .stream()
                .sorted(SCHEDULE_ORDER)
                .toList();

        return TeacherScheduleDTO.builder()
                .teacherId(teacher.getTeacherId())
                .teacherName(fullName(teacher.getUserId()))
                .department(teacher.getDepartment())
                .entries(entries)
                .build();
    }

    private static boolean isAdministrator(UserDetails caller) {
        return caller != null && caller.getAuthorities().stream()
                .anyMatch(a -> ADMIN_AUTHORITY.equals(a.getAuthority()));
    }

    private static boolean isOwner(TeacherEntity teacher, UserDetails caller) {
        UserEntity user = teacher.getUserId();
        return caller != null && user != null && user.getEmail() != null
                && user.getEmail().equalsIgnoreCase(caller.getUsername());
    }

    private static String fullName(UserEntity user) {
        if (user == null) {
            return null;
        }
        String name = Stream.of(user.getFirstName(), user.getLastName())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining(" "));
        return name.isEmpty() ? null : name;
    }

    private static int dayIndex(String dayName) {
        int index = dayName == null ? -1 : WEEK_ORDER.indexOf(dayName.toLowerCase(Locale.ROOT));
        return index < 0 ? WEEK_ORDER.size() : index;
    }
}
