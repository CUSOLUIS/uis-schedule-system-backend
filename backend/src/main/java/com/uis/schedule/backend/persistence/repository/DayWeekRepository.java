package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.DayWeekEntity;

public interface DayWeekRepository extends JpaRepository<DayWeekEntity, Long> {

}

