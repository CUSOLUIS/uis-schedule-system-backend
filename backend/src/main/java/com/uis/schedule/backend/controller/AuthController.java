package com.uis.schedule.backend.controller;

import com.uis.schedule.backend.dto.ApiResponse;
import com.uis.schedule.backend.dto.LoginRequest;
import com.uis.schedule.backend.dto.LoginResponse;
import com.uis.schedule.backend.dto.UserDTO;
import com.uis.schedule.backend.dto.RoleDTO;
import com.uis.schedule.backend.configuration.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:80"})
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/log-in")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            // Autenticación básica para pruebas (reemplazar con DB query)
            if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
                
                // Crear rol completo como espera Angular
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

                // Crear usuario con rol completo (OBJETO COMPLETO no solo ID)
                UserDTO user = UserDTO.builder()
                        .id("1")
                        .username("admin")
                        .fullName("Administrator User")
                        .email("admin@uis.edu.co")
                        .role(role) // CRÍTICO: Objeto completo para Angular
                        .build();

                // Generar token JWT usando el JwtUtil existente
                String token = jwtUtil.generateToken(request.getUsername(), "ADMIN");

                // Respuesta en el formato exacto que espera Angular
                LoginResponse response = LoginResponse.builder()
                        .success(true)
                        .user(user)
                        .token(token)
                        .message("Authentication successful")
                        .build();
                        
                return ResponseEntity.ok(response);
                
            } else {
                LoginResponse errorResponse = LoginResponse.builder()
                        .success(false)
                        .message("Invalid username or password")
                        .build();
                return ResponseEntity.status(401).body(errorResponse);
            }
        } catch (Exception e) {
            LoginResponse errorResponse = LoginResponse.builder()
                    .success(false)
                    .message("Authentication error: " + e.getMessage())
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyToken(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("No token provided"));
            }
            
            String cleanToken = token.replace("Bearer ", "");
            if (jwtUtil.isTokenExpired(cleanToken)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Token expired"));
            }
            return ResponseEntity.ok(ApiResponse.success("Token is valid", "Token verified"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid token"));
        }
    }
}
