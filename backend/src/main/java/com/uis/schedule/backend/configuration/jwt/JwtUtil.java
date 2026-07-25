package com.uis.schedule.backend.configuration.jwt;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtUtil {

    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${security.jwt.key.private}")
    private String secret;

    @Value("${security.jwt.access-token.expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${security.jwt.refresh-token.expiration-ms}")
    private long refreshTokenExpirationMs;

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public String extractJti(String token) {
        return extractClaims(token, Claims::getId);
    }

    public String extractTokenType(String token) {
        return extractClaims(token, claims -> claims.get("type", String.class));
    }

    public Date extractExpiration(String token) {
        return new Date(extractClaims(token, Claims::getExpiration).getTime());
    }

    public LocalDateTime extractExpirationAsLocalDateTime(String token) {
        java.util.Date expiration = extractClaims(token, Claims::getExpiration);
        return LocalDateTime.ofInstant(expiration.toInstant(), java.time.ZoneId.systemDefault());
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    /**
     * Genera el access token: token de corta duración usado para autenticar
     * cada solicitud (validado en {@code JwtFilter}).
     */
    public String generateToken(UUID id, String username, String email, java.util.Collection<String> roles, boolean status) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("roles", roles);
        claims.put("username", username);
        claims.put("email", email);
        claims.put("status", status);
        claims.put("type", TOKEN_TYPE_ACCESS);
        return createToken(claims, email, accessTokenExpirationMs);
    }

    /**
     * Genera el refresh token: token de larga duración cuyo único propósito
     * es solicitar un nuevo access token en {@code POST /auth/refresh}. No
     * se usa para autenticar solicitudes normales.
     */
    public String generateRefreshToken(UUID id, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("type", TOKEN_TYPE_REFRESH);
        return createToken(claims, email, refreshTokenExpirationMs);
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + expirationMs))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String extractedUsername = extractUserName(token);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
