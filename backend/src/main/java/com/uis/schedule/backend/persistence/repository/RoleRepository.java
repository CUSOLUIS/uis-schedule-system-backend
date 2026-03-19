package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    /**
     * Find a role by its name.
     * 
     * @param name the role name
     * @return Optional containing the role if found
     */
    Optional<RoleEntity> findByName(String name);

    /**
     * Find all active roles.
     * 
     * @return List of active roles
     */
    List<RoleEntity> findByIsActiveTrue();

    /**
     * Find all active roles with pagination.
     * 
     * @param pageable pagination info
     * @return Page of active roles
     */
    org.springframework.data.domain.Page<RoleEntity> findAllByIsActiveTrue(org.springframework.data.domain.Pageable pageable);

    /**
     * Find roles by active status with pagination.
     * 
     * @param isActive active status
     * @param pageable pagination info
     * @return Page of roles by status
     */
    org.springframework.data.domain.Page<RoleEntity> findAllByIsActive(boolean isActive, org.springframework.data.domain.Pageable pageable);
}
