package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface ClassHourRepository extends JpaRepository<ClassHourEntity, Long> {

    Page<ClassHourEntity> findAllByIsActiveTrue(Pageable pageable);

    Page<ClassHourEntity> findAllByIsActive(boolean isActive, Pageable pageable);

    Page<ClassHourEntity> findByGroupId_GroupId(Long groupId, Pageable pageable);

    Page<ClassHourEntity> findByClassroomId_ClassroomId(Long classroomId, Pageable pageable);

    /**
     * Find active class hours that overlap with the given time range
     * on any of the specified days, in a specific classroom,
     * excluding a given class hour ID.
     * <p>
     * Overlap condition: existing.startTime < newEndTime AND existing.endTime > newStartTime
     */
    @Query("SELECT ch FROM ClassHourEntity ch " +
           "JOIN ch.days d " +
           "WHERE d.dayId IN :dayIds " +
           "AND ch.classroomId.classroomId = :classroomId " +
           "AND ch.isActive = true " +
           "AND ch.classHourId <> :excludeId " +
           "AND ch.startTime < :endTime " +
           "AND ch.endTime > :startTime")
    List<ClassHourEntity> findOverlappingHours(
            @Param("dayIds") List<Long> dayIds,
            @Param("classroomId") Long classroomId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );

    /**
     * Find active class hours that overlap with the given time range
     * on any of the specified days, in a specific classroom.
     * Used for creating new class hours (no ID to exclude).
     */
    @Query("SELECT ch FROM ClassHourEntity ch " +
           "JOIN ch.days d " +
           "WHERE d.dayId IN :dayIds " +
           "AND ch.classroomId.classroomId = :classroomId " +
           "AND ch.isActive = true " +
           "AND ch.startTime < :endTime " +
           "AND ch.endTime > :startTime")
    List<ClassHourEntity> findOverlappingHoursForCreate(
            @Param("dayIds") List<Long> dayIds,
            @Param("classroomId") Long classroomId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
