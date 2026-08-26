package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
              AND (:excludeId IS NULL OR c.classroomId <> :excludeId)
            """)
    boolean existsActiveDuplicate(
            @Param("number") String number,
            @Param("campus") String campus,
            @Param("building") String building,
            @Param("excludeId") UUID excludeId
    );

    @Query("""
            SELECT c FROM ClassroomEntity c
            WHERE c.isActive = true
              AND (:name IS NULL OR :name = '' OR LOWER(c.number) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:building IS NULL OR :building = '' OR LOWER(c.building) LIKE LOWER(CONCAT('%', :building, '%')))
              AND (:capacity IS NULL OR c.maxCapacity >= :capacity)
            """)
    Page<ClassroomEntity> searchActive(
            @Param("name") String name,
            @Param("building") String building,
            @Param("capacity") Integer capacity,
            Pageable pageable
    );
}
