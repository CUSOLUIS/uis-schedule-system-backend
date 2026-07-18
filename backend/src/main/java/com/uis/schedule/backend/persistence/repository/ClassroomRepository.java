package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.ClassroomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClassroomRepository extends JpaRepository<ClassroomEntity, UUID> {

    Page<ClassroomEntity> findAllByIsActiveTrue(Pageable pageable);

    Page<ClassroomEntity> findAllByIsActive(boolean isActive, Pageable pageable);
}
