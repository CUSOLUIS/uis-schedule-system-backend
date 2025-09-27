package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.GroupEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

}

