package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleEntryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ClassHourRepository extends JpaRepository<ClassHourEntity, UUID> {

    Page<ClassHourEntity> findAllByIsActiveTrue(Pageable pageable);

    Page<ClassHourEntity> findAllByIsActiveTrueAndGroupId_IsActiveTrue(Pageable pageable);

    Page<ClassHourEntity> findAllByIsActive(boolean isActive, Pageable pageable);

    Page<ClassHourEntity> findByGroupId_GroupId(UUID groupId, Pageable pageable);

    Page<ClassHourEntity> findByGroupId_GroupIdAndIsActiveTrueAndGroupId_IsActiveTrue(UUID groupId, Pageable pageable);

    Page<ClassHourEntity> findByClassroomId_ClassroomId(UUID classroomId, Pageable pageable);

    Page<ClassHourEntity> findByClassroomId_ClassroomIdAndIsActiveTrueAndGroupId_IsActiveTrue(UUID classroomId, Pageable pageable);

    List<ClassHourEntity> findAllByIsActiveTrueAndClassroomId_ClassroomId(UUID classroomId);

    List<ClassHourEntity> findAllByGroupId_GroupIdAndIsActiveTrue(UUID groupId);

    Page<ClassHourEntity> findByDays_DayIdAndIsActiveTrueAndGroupId_IsActiveTrue(UUID dayId, Pageable pageable);

    Page<ClassHourEntity> findByGroupId_TeacherId_TeacherIdAndIsActiveTrueAndGroupId_IsActiveTrue(UUID teacherId, Pageable pageable);

    /**
     * Find active class hours that overlap with the given time range and date range
     * on any of the specified days, in a specific classroom,
     * excluding a given class hour ID.
     * <p>
     * Time overlap: existing.startTime < newEndTime AND existing.endTime > newStartTime
     * Date overlap: existing.startDate <= newEndDate AND existing.endDate >= newStartDate
     */
    @Query("SELECT ch FROM ClassHourEntity ch " +
           "JOIN ch.days d " +
           "WHERE d.dayId IN :dayIds " +
           "AND ch.classroomId.classroomId = :classroomId " +
           "AND ch.isActive = true " +
           "AND (ch.groupId IS NULL OR ch.groupId.isActive = true) " +
           "AND ch.classHourId <> :excludeId " +
           "AND ch.startTime < :endTime " +
           "AND ch.endTime > :startTime " +
           "AND ch.startDate <= :endDate " +
           "AND ch.endDate >= :startDate")
    List<ClassHourEntity> findOverlappingHours(
            @Param("dayIds") List<UUID> dayIds,
            @Param("classroomId") UUID classroomId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") UUID excludeId
    );

    /**
     * Find active class hours that overlap with the given time range and date range
     * on any of the specified days, in a specific classroom.
     * Used for creating new class hours (no ID to exclude).
     */
    @Query("SELECT ch FROM ClassHourEntity ch " +
           "JOIN ch.days d " +
           "WHERE d.dayId IN :dayIds " +
           "AND ch.classroomId.classroomId = :classroomId " +
           "AND ch.isActive = true " +
           "AND (ch.groupId IS NULL OR ch.groupId.isActive = true) " +
           "AND ch.startTime < :endTime " +
           "AND ch.endTime > :startTime " +
           "AND ch.startDate <= :endDate " +
           "AND ch.endDate >= :startDate")
    List<ClassHourEntity> findOverlappingHoursForCreate(
            @Param("dayIds") List<UUID> dayIds,
            @Param("classroomId") UUID classroomId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Overlapping active class hours for the same teacher (via the assigned group),
     * excluding a given class hour ID.
     */
    @Query("SELECT ch FROM ClassHourEntity ch " +
           "JOIN ch.days d " +
           "JOIN ch.groupId g " +
           "WHERE d.dayId IN :dayIds " +
           "AND g.teacherId.teacherId = :teacherId " +
           "AND ch.isActive = true " +
           "AND g.isActive = true " +
           "AND ch.classHourId <> :excludeId " +
           "AND ch.startTime < :endTime " +
           "AND ch.endTime > :startTime " +
           "AND ch.startDate <= :endDate " +
           "AND ch.endDate >= :startDate")
    List<ClassHourEntity> findOverlappingHoursByTeacher(
            @Param("dayIds") List<UUID> dayIds,
            @Param("teacherId") UUID teacherId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") UUID excludeId
    );

    /**
     * Overlapping active class hours for the same teacher (via the assigned group).
     * Used for creating new class hours (no ID to exclude).
     */
    @Query("SELECT ch FROM ClassHourEntity ch " +
           "JOIN ch.days d " +
           "JOIN ch.groupId g " +
           "WHERE d.dayId IN :dayIds " +
           "AND g.teacherId.teacherId = :teacherId " +
           "AND ch.isActive = true " +
           "AND g.isActive = true " +
           "AND ch.startTime < :endTime " +
           "AND ch.endTime > :startTime " +
           "AND ch.startDate <= :endDate " +
           "AND ch.endDate >= :startDate")
    List<ClassHourEntity> findOverlappingHoursByTeacherForCreate(
            @Param("dayIds") List<UUID> dayIds,
            @Param("teacherId") UUID teacherId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Horario semanal del profesor: una fila por franja y día, solo franjas y grupos activos.
     * Se proyecta directo al DTO para no cargar las relaciones EAGER de cada entidad (evita N+1).
     * El aula es la de la franja, que es la que reserva el espacio.
     */
    @Query("SELECT new com.uis.schedule.backend.presentation.dto.TeacherScheduleEntryDTO(" +
           "ch.classHourId, d.name, ch.startTime, ch.endTime, " +
           "g.groupId, g.name, g.capacity, s.code, s.name, s.credits, c.number, c.building) " +
           "FROM ClassHourEntity ch " +
           "JOIN ch.days d " +
           "JOIN ch.groupId g " +
           "JOIN g.subjectId s " +
           "LEFT JOIN ch.classroomId c " +
           "WHERE g.teacherId.teacherId = :teacherId " +
           "AND ch.isActive = true " +
           "AND g.isActive = true " +
           "ORDER BY ch.startTime")
    List<TeacherScheduleEntryDTO> findScheduleEntriesByTeacherId(@Param("teacherId") UUID teacherId);
}
