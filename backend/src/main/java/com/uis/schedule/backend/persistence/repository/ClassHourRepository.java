package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ClassHourRepository extends JpaRepository<ClassHourEntity, UUID> {

    Page<ClassHourEntity> findAllByIsActiveTrue(Pageable pageable);

    Page<ClassHourEntity> findAllByIsActive(boolean isActive, Pageable pageable);

    Page<ClassHourEntity> findByGroupId_GroupId(UUID groupId, Pageable pageable);

    Page<ClassHourEntity> findByClassroomId_ClassroomId(UUID classroomId, Pageable pageable);

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
            @Param("dayIds") List<UUID> dayIds,
            @Param("classroomId") UUID classroomId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") UUID excludeId
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
            @Param("dayIds") List<UUID> dayIds,
            @Param("classroomId") UUID classroomId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
