package com.uis.schedule.backend.persistence.repository;

import com.uis.schedule.backend.persistence.entity.UserInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInvitationRepository extends JpaRepository<UserInvitationEntity, UUID> {
    Optional<UserInvitationEntity> findByToken(String token);
    List<UserInvitationEntity> findByStatus(String status);
    boolean existsByEmailAndStatus(String email, String status);
    boolean existsByUsernameAndStatusIn(String username, List<String> statuses);
}