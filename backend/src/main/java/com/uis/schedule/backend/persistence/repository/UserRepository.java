package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.uis.schedule.backend.persistence.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = { "roles" })
    Optional<UserEntity> findUserEntityByEmailOrUsername(String email, String username);
}
