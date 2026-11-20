package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ClassroomRepository extends JpaRepository<ClassroomEntity, UUID> {

    Page<ClassroomEntity> findAllByIsActiveTrue(Pageable pageable);

    Page<ClassroomEntity> findAllByIsActive(boolean isActive, Pageable pageable);

    @Query("""
            SELECT COUNT(c) > 0 FROM ClassroomEntity c
            WHERE LOWER(c.number) = LOWER(:number)
              AND LOWER(COALESCE(c.campus, '')) = LOWER(COALESCE(:campus, ''))
              AND LOWER(COALESCE(c.building, '')) = LOWER(COALESCE(:building, ''))
              AND c.isActive = true
            """)
    boolean existsActiveDuplicateForCreate(
            @Param("number") String number,
            @Param("campus") String campus,
            @Param("building") String building
    );

    @Query("""
            SELECT COUNT(c) > 0 FROM ClassroomEntity c
            WHERE LOWER(c.number) = LOWER(:number)
              AND LOWER(COALESCE(c.campus, '')) = LOWER(COALESCE(:campus, ''))
              AND LOWER(COALESCE(c.building, '')) = LOWER(COALESCE(:building, ''))
              AND c.isActive = true
              AND c.classroomId <> :excludeId
            """)
    boolean existsActiveDuplicateExcluding(
            @Param("number") String number,
            @Param("campus") String campus,
            @Param("building") String building,
            @Param("excludeId") UUID excludeId
    );

    @Query("""
            SELECT c FROM ClassroomEntity c
            WHERE c.isActive = true
              AND (:name IS NULL OR :name = '' OR LOWER(c.number) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:campus IS NULL OR :campus = '' OR LOWER(c.campus) LIKE LOWER(CONCAT('%', :campus, '%')))
              AND (:building IS NULL OR :building = '' OR LOWER(c.building) LIKE LOWER(CONCAT('%', :building, '%')))
              AND (:capacity IS NULL OR c.maxCapacity >= :capacity)
            """)
    Page<ClassroomEntity> searchActive(
            @Param("name") String name,
            @Param("campus") String campus,
            @Param("building") String building,
            @Param("capacity") Integer capacity,
            Pageable pageable
    );

    /**
     * Active classrooms free for the given slot, ordered by building preference
     * then smallest sufficient capacity.
     */
    @Query("""
            SELECT c FROM ClassroomEntity c
            LEFT JOIN c.buildingId b
            WHERE c.isActive = true
              AND (c.maxCapacity IS NULL OR c.maxCapacity >= :minCapacity)
              AND c.classroomId NOT IN (
                  SELECT ch.classroomId.classroomId FROM ClassHourEntity ch
                  JOIN ch.days d
                  WHERE d.dayId = :dayId
                    AND ch.isActive = true
                    AND (ch.groupId IS NULL OR ch.groupId.isActive = true)
                    AND ch.classroomId IS NOT NULL
                    AND ch.startTime < :endTime
                    AND ch.endTime > :startTime
                    AND ch.startDate <= :endDate
                    AND ch.endDate >= :startDate
                    AND (:excludeClassHourId IS NULL OR ch.classHourId <> :excludeClassHourId)
              )
            ORDER BY COALESCE(b.preferenceOrder, 9999) ASC,
                     COALESCE(c.maxCapacity, 999999) ASC
            """)
    List<ClassroomEntity> findAvailableForSlot(
            @Param("dayId") UUID dayId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minCapacity") Integer minCapacity,
            @Param("excludeClassHourId") UUID excludeClassHourId
    );
}
