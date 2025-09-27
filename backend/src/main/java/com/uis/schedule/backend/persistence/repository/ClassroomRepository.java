package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.ClassroomEntity;

public interface ClassroomRepository extends JpaRepository<ClassroomEntity, Long> {

}

