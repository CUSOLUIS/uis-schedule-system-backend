package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {

    Page<GroupEntity> findAllByIsActiveTrue(Pageable pageable);

    Page<GroupEntity> findAllByIsActive(boolean isActive, Pageable pageable);

    Page<GroupEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<GroupEntity> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    Page<GroupEntity> findByClassroomId_ClassroomId(UUID classroomId, Pageable pageable);

    Page<GroupEntity> findByClassroomId_ClassroomIdAndIsActiveTrue(UUID classroomId, Pageable pageable);

    Page<GroupEntity> findBySubjectId_SubjectId(UUID subjectId, Pageable pageable);

    Page<GroupEntity> findBySubjectId_SubjectIdAndIsActiveTrue(UUID subjectId, Pageable pageable);

    boolean existsByClassroomId_ClassroomIdAndIsActiveTrue(UUID classroomId);

    List<GroupEntity> findAllByClassroomId_ClassroomIdAndIsActiveTrue(UUID classroomId);

    @Query("""
            SELECT COUNT(g) > 0 FROM GroupEntity g
            WHERE LOWER(g.name) = LOWER(:name)
              AND g.subjectId.subjectId = :subjectId
              AND g.periodId.periodId = :periodId
              AND g.isActive = true
              AND (:excludeGroupId IS NULL OR g.groupId <> :excludeGroupId)
            """)
    boolean existsActiveDuplicate(
            @Param("name") String name,
            @Param("subjectId") UUID subjectId,
            @Param("periodId") UUID periodId,
            @Param("excludeGroupId") UUID excludeGroupId
    );
}
