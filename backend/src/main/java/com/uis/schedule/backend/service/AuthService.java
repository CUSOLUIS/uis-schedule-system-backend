package com.uis.schedule.backend.service;

import com.uis.schedule.backend.dto.LoginRequest;
import com.uis.schedule.backend.dto.LoginResponse;
import com.uis.schedule.backend.dto.UserDTO;
import com.uis.schedule.backend.dto.RoleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    public LoginResponse authenticate(LoginRequest request) {
        // TODO: Implementar autenticación real con base de datos
        // Por ahora, usuario de prueba
        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
            
            // Crear rol completo
            RoleDTO role = RoleDTO.builder()
                    .id("1")
                    .name("ADMIN")
                    .displayName("Administrator")
                    .description("System Administrator")
                    .permissions(Arrays.asList("READ", "WRITE", "DELETE", "ADMIN"))
                    .colorScheme("primary")
                    .iconClass("fa-user-shield")
                    .routes(Arrays.asList("/dashboard", "/users", "/settings"))
                    .build();

            // Crear usuario con rol completo
            UserDTO user = UserDTO.builder()
                    .id("1")
                    .username("admin")
                    .fullName("Administrator User")
                    .email("admin@uis.edu.co")
                    .role(role) // OBJETO COMPLETO
                    .build();

            // Generar token JWT
            String token = jwtService.generateToken(user);

            return LoginResponse.builder()
                    .success(true)
                    .user(user)
                    .token(token)
                    .message("Authentication successful")
                    .build();
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}