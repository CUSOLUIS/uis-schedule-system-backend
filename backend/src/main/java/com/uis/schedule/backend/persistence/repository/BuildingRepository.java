package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.BuildingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository extends JpaRepository<BuildingEntity, UUID> {

    Optional<BuildingEntity> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    Page<BuildingEntity> findAllByIsActiveTrue(Pageable pageable);
}
