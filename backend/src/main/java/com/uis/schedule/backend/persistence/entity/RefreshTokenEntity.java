package com.uis.schedule.backend.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Esta entidad representa un refresh token emitido a un usuario. Se mantiene como
 * "whitelist": solo los refresh tokens presentes aquí (y no revocados ni
 * expirados) pueden usarse para obtener un nuevo access token.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_token")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "refresh_token_id")
    private UUID refreshTokenId;

    @Column(nullable = false, unique = true, length = 255)
    private String jti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;
}
