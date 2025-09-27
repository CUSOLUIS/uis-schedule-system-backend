package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.ScheduleEntity;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

}

