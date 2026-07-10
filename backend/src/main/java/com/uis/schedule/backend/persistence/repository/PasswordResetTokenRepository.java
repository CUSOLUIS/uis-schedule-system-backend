package com.uis.schedule.backend.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uis.schedule.backend.persistence.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {

    Optional<PasswordResetTokenEntity> findByToken(String token);

    void deleteByUserUserId(UUID userId);
}
