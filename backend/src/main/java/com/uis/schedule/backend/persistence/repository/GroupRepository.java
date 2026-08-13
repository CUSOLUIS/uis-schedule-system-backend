package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
