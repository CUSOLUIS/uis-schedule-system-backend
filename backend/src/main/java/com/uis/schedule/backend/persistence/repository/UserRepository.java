package com.uis.schedule.backend.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.uis.schedule.backend.persistence.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

  @EntityGraph(attributePaths = { "roles" })
  Optional<UserEntity> findUserEntityByEmailOrUsername(String email, String username);

  Page<UserEntity> findAllByIsEnable(boolean isEnable, Pageable pageable);

  @EntityGraph(attributePaths = { "roles" })
  Page<UserEntity> findByRolesName(String roleName, Pageable pageable);

  Page<UserEntity> findAll(Pageable pageable);

  Optional<UserEntity> findByUserIdAndIsEnableTrue(UUID userId);

  // Verifica si ya existe un usuario con ese username
  boolean existsByUsername(String username);

  // Verifica si ya existe un usuario con ese email
  boolean existsByEmail(String email);

  boolean existsByRoles_Guid(UUID roleGuid);
}
