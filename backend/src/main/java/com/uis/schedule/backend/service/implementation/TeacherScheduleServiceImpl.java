package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.repository.ClassHourRepository;
import com.uis.schedule.backend.persistence.repository.TeacherRepository;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleDTO;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleEntryDTO;
import com.uis.schedule.backend.service.exception.TeacherNotFoundException;
import com.uis.schedule.backend.service.interfaces.TeacherScheduleService;
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
public class TeacherScheduleServiceImpl implements TeacherScheduleService {

    private static final List<String> WEEK_ORDER =
            List.of("lunes", "martes", "miércoles", "miercoles", "jueves", "viernes", "sábado", "sabado", "domingo");

    private static final Comparator<TeacherScheduleEntryDTO> SCHEDULE_ORDER =
            Comparator.comparingInt((TeacherScheduleEntryDTO e) -> dayIndex(e.getDayName()))
                    .thenComparing(TeacherScheduleEntryDTO::getStartTime,
                            Comparator.nullsLast(Comparator.<LocalTime>naturalOrder()));

    private final TeacherRepository teacherRepository;
    private final ClassHourRepository classHourRepository;

    @Override
    public TeacherScheduleDTO getScheduleByTeacherId(UUID teacherId) {
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID is required");
        }
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException(teacherId));

        List<TeacherScheduleEntryDTO> entries = classHourRepository
                .findScheduleEntriesByTeacherId(teacher.getTeacherId())
                .stream()
                .sorted(SCHEDULE_ORDER)
                .toList();

        return TeacherScheduleDTO.builder()
                .teacherId(teacher.getTeacherId())
                .teacherName(teacher.getFullName())
                .linkType(teacher.getLinkType())
                .department(teacher.getDepartment())
                .entries(entries)
                .build();
    }

    private static int dayIndex(String dayName) {
        if (dayName == null) {
            return WEEK_ORDER.size();
        }
        String normalized = dayName.toLowerCase(Locale.ROOT);
        int index = WEEK_ORDER.indexOf(normalized);
        return index < 0 ? WEEK_ORDER.size() : index;
    }
}
