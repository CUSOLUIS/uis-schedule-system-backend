package com.uis.schedule.backend.service;

import com.uis.schedule.backend.dto.UserDTO;
import com.uis.schedule.backend.dto.RoleDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;

@Service
public class UserService {

    public List<UserDTO> getAllUsers() {
        // Datos de prueba para Angular - En producción conectar con DB
        RoleDTO adminRole = RoleDTO.builder()
                .id("1")
                .name("ADMIN")
                .displayName("Administrator")
                .description("System Administrator")
                .permissions(Arrays.asList("READ", "WRITE", "DELETE", "ADMIN"))
                .colorScheme("primary")
                .iconClass("fa-user-shield")
                .routes(Arrays.asList("/dashboard", "/users", "/settings"))
                .build();

        RoleDTO userRole = RoleDTO.builder()
                .id("2")
                .name("USER")
                .displayName("User")
                .description("Regular User")
                .permissions(Arrays.asList("READ"))
                .colorScheme("secondary")
                .iconClass("fa-user")
                .routes(Arrays.asList("/dashboard"))
                .build();

        RoleDTO teacherRole = RoleDTO.builder()
                .id("3")
                .name("TEACHER")
                .displayName("Teacher")
                .description("Teaching Staff")
                .permissions(Arrays.asList("READ", "WRITE"))
                .colorScheme("success")
                .iconClass("fa-chalkboard-teacher")
                .routes(Arrays.asList("/dashboard", "/classes"))
                .build();

        return Arrays.asList(
            UserDTO.builder()
                .id("1")
                .username("admin")
                .fullName("Administrator User")
                .email("admin@uis.edu.co")
                .role(adminRole) // OBJETO COMPLETO para Angular
                .build(),
            UserDTO.builder()
                .id("2")
                .username("teacher1")
                .fullName("John Teacher")
                .email("teacher1@uis.edu.co")
                .role(teacherRole) // OBJETO COMPLETO para Angular
                .build(),
            UserDTO.builder()
                .id("3")
                .username("student1")
                .fullName("Jane Student")
                .email("student1@uis.edu.co")
                .role(userRole) // OBJETO COMPLETO para Angular
                .build()
        );
    }

    public UserDTO getUserById(String id) {
        // Simular búsqueda por ID
        List<UserDTO> users = getAllUsers();
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public UserDTO createUser(UserDTO userDTO) {
        // Simular creación - En producción guardar en DB
        userDTO.setId("new-id-" + System.currentTimeMillis());
        
        // Si no tiene rol, asignar rol por defecto
        if (userDTO.getRole() == null) {
            RoleDTO defaultRole = RoleDTO.builder()
                    .id("2")
                    .name("USER")
                    .displayName("User")
                    .description("Regular User")
                    .permissions(Arrays.asList("READ"))
                    .colorScheme("secondary")
                    .iconClass("fa-user")
                    .routes(Arrays.asList("/dashboard"))
                    .build();
            userDTO.setRole(defaultRole);
        }
        
        return userDTO;
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {
        // Simular actualización - En producción actualizar en DB
        UserDTO existingUser = getUserById(id);
        if (existingUser != null) {
            userDTO.setId(id);
            return userDTO;
        }
        return null;
    }

    public boolean deleteUser(String id) {
        // Simular eliminación - En producción eliminar de DB
        UserDTO user = getUserById(id);
        return user != null;
    }

    public List<UserDTO> searchUsers(String query) {
        // Simular búsqueda - En producción buscar en DB
        List<UserDTO> allUsers = getAllUsers();
        if (query == null || query.trim().isEmpty()) {
            return allUsers;
        }
        
        String lowerQuery = query.toLowerCase();
        return allUsers.stream()
                .filter(user -> 
                    user.getUsername().toLowerCase().contains(lowerQuery) ||
                    user.getFullName().toLowerCase().contains(lowerQuery) ||
                    user.getEmail().toLowerCase().contains(lowerQuery)
                )
                .toList();
    }
}