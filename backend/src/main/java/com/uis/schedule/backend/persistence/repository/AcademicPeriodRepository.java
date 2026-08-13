package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.AcademicPeriodEntity;
import java.util.UUID;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriodEntity, UUID> {

}

