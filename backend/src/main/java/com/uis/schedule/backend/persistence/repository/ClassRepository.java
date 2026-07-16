package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.ClassEntity;
import java.util.UUID;

public interface ClassRepository extends JpaRepository<ClassEntity, UUID> {

}

