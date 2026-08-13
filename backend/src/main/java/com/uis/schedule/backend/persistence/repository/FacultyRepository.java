package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.FacultyEntity;
import java.util.UUID;

public interface FacultyRepository extends JpaRepository<FacultyEntity, UUID> {

}

