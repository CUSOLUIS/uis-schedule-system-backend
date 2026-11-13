package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.AcademicPeriodEntity;
import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import com.uis.schedule.backend.persistence.entity.DayWeekEntity;
import com.uis.schedule.backend.persistence.entity.GroupEntity;
import com.uis.schedule.backend.persistence.entity.SubjectEntity;
import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleEntryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la consulta JPQL real del horario del profesor sobre H2.
 */
@DataJpaTest
class ClassHourRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ClassHourRepository classHourRepository;

    private TeacherEntity teacher;
    private TeacherEntity otherTeacher;
    private AcademicPeriodEntity period;
    private SubjectEntity subject;
    private ClassroomEntity classroom;
    private DayWeekEntity monday;
    private DayWeekEntity wednesday;

    @BeforeEach
    void setUp() {
        teacher = persistTeacher("docente@uis.edu.co");
        otherTeacher = persistTeacher("otro@uis.edu.co");
        period = em.persist(AcademicPeriodEntity.builder()
                .name("2026-2").startDate(LocalDate.of(2026, 7, 1)).endDate(LocalDate.of(2026, 12, 1)).active(true)
                .build());
        subject = em.persist(SubjectEntity.builder().code("22966").name("Bases de Datos").credits(4).build());
        classroom = em.persist(ClassroomEntity.builder()
                .number("301").building("Ingeniería de Sistemas").maxCapacity(40).isActive(true).build());
        monday = em.persist(DayWeekEntity.builder().name("lunes").build());
        wednesday = em.persist(DayWeekEntity.builder().name("miércoles").build());
    }

    @Test
    void findScheduleEntriesByTeacherId_returnsOneRowPerDayWithJoinedNames() {
        GroupEntity group = persistGroup(teacher, "A1", true);
        persistClassHour(group, classroom, Set.of(monday, wednesday), "08:00", true);

        List<TeacherScheduleEntryDTO> entries = classHourRepository.findScheduleEntriesByTeacherId(teacher.getTeacherId());

        assertEquals(2, entries.size());
        assertEquals(Set.of("lunes", "miércoles"),
                Set.copyOf(entries.stream().map(TeacherScheduleEntryDTO::getDayName).toList()));
        TeacherScheduleEntryDTO first = entries.get(0);
        assertEquals("Bases de Datos", first.getSubjectName());
        assertEquals("22966", first.getSubjectCode());
        assertEquals(4, first.getSubjectCredits());
        assertEquals("A1", first.getGroupName());
        assertEquals(35, first.getGroupCapacity());
        assertEquals("301", first.getClassroomNumber());
        assertEquals(LocalTime.of(8, 0), first.getStartTime());
        assertEquals(LocalTime.of(10, 0), first.getEndTime());
    }

    @Test
    void findScheduleEntriesByTeacherId_excludesInactiveHoursInactiveGroupsAndOtherTeachers() {
        persistClassHour(persistGroup(teacher, "A1", true), classroom, Set.of(monday), "08:00", false);
        persistClassHour(persistGroup(teacher, "B1", false), classroom, Set.of(monday), "10:00", true);
        persistClassHour(persistGroup(otherTeacher, "C1", true), classroom, Set.of(monday), "14:00", true);

        assertTrue(classHourRepository.findScheduleEntriesByTeacherId(teacher.getTeacherId()).isEmpty());
    }

    @Test
    void findScheduleEntriesByTeacherId_keepsHoursWithoutClassroom() {
        persistClassHour(persistGroup(teacher, "A1", true), null, Set.of(monday), "08:00", true);

        List<TeacherScheduleEntryDTO> entries = classHourRepository.findScheduleEntriesByTeacherId(teacher.getTeacherId());

        assertEquals(1, entries.size());
        assertNull(entries.get(0).getClassroomNumber());
    }

    private TeacherEntity persistTeacher(String email) {
        UserEntity user = em.persist(UserEntity.builder()
                .email(email).password("x").username(email).firstName("Ana").lastName("Pérez").build());
        return em.persist(TeacherEntity.builder().department("Ingeniería de Sistemas").userId(user).build());
    }

    private GroupEntity persistGroup(TeacherEntity owner, String name, boolean active) {
        return em.persist(GroupEntity.builder()
                .name(name).capacity(35).teacherId(owner).periodId(period).subjectId(subject).isActive(active)
                .build());
    }

    private void persistClassHour(GroupEntity group, ClassroomEntity room, Set<DayWeekEntity> days,
                                  String start, boolean active) {
        LocalTime startTime = LocalTime.parse(start);
        em.persist(ClassHourEntity.builder()
                .groupId(group).classroomId(room).days(new HashSet<>(days))
                .startTime(startTime).endTime(startTime.plusHours(2))
                .startDate(LocalDate.of(2026, 7, 16)).endDate(LocalDate.of(2026, 11, 20))
                .isActive(active)
                .build());
        em.flush();
    }
}
