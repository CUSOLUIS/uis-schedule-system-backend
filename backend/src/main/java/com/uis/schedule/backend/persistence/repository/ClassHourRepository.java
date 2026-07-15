package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.ClassHourEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassHourRepository extends JpaRepository<ClassHourEntity, Long> {

    Page<ClassHourEntity> findAllByIsActiveTrue(Pageable pageable);

    Page<ClassHourEntity> findAllByIsActive(boolean isActive, Pageable pageable);

    Page<ClassHourEntity> findByGroupId_GroupId(Long groupId, Pageable pageable);

    Page<ClassHourEntity> findByClassroomId_ClassroomId(Long classroomId, Pageable pageable);
}
