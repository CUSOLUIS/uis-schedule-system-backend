package com.uis.schedule.backend.configuration.jwt;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {
    @Value("${security.jwt.key.private}")
    private String secret;

    public String extractUserName (String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration (String token) {
        return new Date(extractClaims(token, Claims::getExpiration).getTime());
    }

    public <T> T extractClaims (String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    Claims extractAllClaims (String token) {
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
    }

    // Método público para verificar si el token ha expirado
    public Boolean isTokenExpired (String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    // Método sobrecargado para solo username (compatibilidad con Angular)
    public String generateToken (String username) {
        return generateToken(username, "USER");
    }

    public String generateToken (String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken (Map<String, Object> claims, String subject) {
        long expirationTime = 1000 * 60 * 60 * 24; // 24 hours para Angular
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + expirationTime))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, secret.getBytes())
                .compact();
    }

    public Boolean validateToken (String token, UserDetails userDetails) {
        final String extractedUsername = extractUserName(token);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
