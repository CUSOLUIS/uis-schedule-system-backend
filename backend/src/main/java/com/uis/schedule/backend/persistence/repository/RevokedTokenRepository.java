package com.uis.schedule.backend.persistence.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.uis.schedule.backend.persistence.entity.RevokedTokenEntity;

public interface RevokedTokenRepository extends JpaRepository<RevokedTokenEntity, UUID> {

    boolean existsByJti(String jti);

    /**
     * Elimina los tokens revocados cuya expiración natural ya pasó,
     * dado que dejan de ser necesarios para el chequeo de la blacklist.
     */
    @Modifying
    @Query("DELETE FROM RevokedTokenEntity r WHERE r.expiresAt < :now")
    int deleteAllExpiredBefore(LocalDateTime now);
}
