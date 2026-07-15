package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    Page<GroupEntity> findAllByIsActiveTrue(Pageable pageable);

    Page<GroupEntity> findAllByIsActive(boolean isActive, Pageable pageable);
}
