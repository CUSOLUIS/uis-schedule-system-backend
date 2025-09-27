package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.SubjectEntity;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {

}
