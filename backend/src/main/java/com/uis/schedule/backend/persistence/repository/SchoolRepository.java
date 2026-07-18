package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.SchoolEntity;
import java.util.UUID;

public interface SchoolRepository extends JpaRepository<SchoolEntity, UUID> {

}

