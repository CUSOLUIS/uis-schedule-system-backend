package com.uis.schedule.backend.persistence.repository;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import com.uis.schedule.backend.persistence.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String, String> requesMap);

    UserEntity findByEmail(@Param("email") String email);
}
