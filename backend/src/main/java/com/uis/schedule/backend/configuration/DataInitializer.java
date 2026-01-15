package com.uis.schedule.backend.configuration;

import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.RoleRepository;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Data initializer to populate the database with default roles and sample
 * users.
 * Runs on application startup.
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");

        RoleEntity administradorRole = null;
        RoleEntity operadorRole = null;
        RoleEntity docenteRole = null;
        RoleEntity estudianteRole = null;

        try {
            // Initialize roles
            administradorRole = initializeRole("ADMINISTRADOR");
            operadorRole = initializeRole("OPERADOR");
            docenteRole = initializeRole("DOCENTE");
            estudianteRole = initializeRole("ESTUDIANTE");

            log.info("Roles initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing roles: ", e);
            throw e;
        }

        try {
            // Initialize sample users only if no users exist
            if (userRepository.count() == 0) {
                log.info("No users found. Creating sample users...");

                UserEntity nicoleUser = UserEntity.builder()
                        .firstName("Nicole")
                        .lastName("Alvarez")
                        .username("nalvarez")
                        .email("nicole0202@uis.edu.co")
                        .password(passwordEncoder.encode("1234"))
                        .isEnable(true)
                        .accountNoExpired(true)
                        .accountNoLocked(true)
                        .credentialNoExpired(true)
                        .roles(new java.util.HashSet<>(java.util.List.of(administradorRole)))
                        .build();

                UserEntity marcosUser = UserEntity.builder()
                        .firstName("Marcos")
                        .lastName("Perez")
                        .username("mperez")
                        .email("Marcos123@uis.edu.co")
                        .password(passwordEncoder.encode("1234"))
                        .isEnable(true)
                        .accountNoExpired(true)
                        .accountNoLocked(true)
                        .credentialNoExpired(true)
                        .roles(new java.util.HashSet<>(java.util.List.of(estudianteRole)))
                        .build();

                UserEntity dayannaUser = UserEntity.builder()
                        .firstName("Dayanna")
                        .lastName("Diaz")
                        .username("ddiaz")
                        .email("Dayanna123@uis.edu.co")
                        .password(passwordEncoder.encode("1234"))
                        .isEnable(true)
                        .accountNoExpired(true)
                        .accountNoLocked(true)
                        .credentialNoExpired(true)
                        .roles(new java.util.HashSet<>(java.util.List.of(operadorRole)))
                        .build();

                UserEntity julianUser = UserEntity.builder()
                        .firstName("Julian")
                        .lastName("Gomez")
                        .username("jgomez")
                        .email("Julian123@uis.edu.co")
                        .password(passwordEncoder.encode("1234"))
                        .isEnable(true)
                        .accountNoExpired(true)
                        .accountNoLocked(true)
                        .credentialNoExpired(true)
                        .roles(new java.util.HashSet<>(java.util.List.of(docenteRole)))
                        .build();

                userRepository.saveAll(List.of(julianUser, nicoleUser, dayannaUser, marcosUser));
                log.info("Sample users created successfully");
            } else {
                log.info("Users already exist. Skipping sample user creation.");
            }
        } catch (Exception e) {
            log.error("Error creating sample users: ", e);
            throw e; // Rethrow to fail startup predictably
        }

        log.info("Data initialization completed");
    }

    /**
     * Initialize a role if it doesn't exist, or return the existing one.
     */
    private RoleEntity initializeRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    RoleEntity role = RoleEntity.builder()
                            .name(roleName)
                            .isActive(true)
                            .build();
                    RoleEntity savedRole = roleRepository.save(role);
                    log.info("Created role: {}", roleName);
                    return savedRole;
                });
    }
}
