package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

}

