package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.TeacherEntity;

public interface TeacherRepository extends JpaRepository<TeacherEntity, Long> {

}
