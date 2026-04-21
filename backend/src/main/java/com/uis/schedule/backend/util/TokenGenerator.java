package com.uis.schedule.backend.util;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Base64;

// Utilidad para generar tokens únicos y seguros para las invitaciones
@Component
public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    // Genera un token aleatorio de 32 bytes codificado en Base64 URL-safe
    public String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}