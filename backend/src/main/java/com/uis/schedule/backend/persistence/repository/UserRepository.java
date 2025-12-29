package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = { "roles", "roles.permissionList" })
    Optional<UserEntity> findUserEntityByEmailOrName(String email, String name);
}
