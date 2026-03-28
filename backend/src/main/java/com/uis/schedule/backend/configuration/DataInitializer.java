package com.uis.schedule.backend.configuration;

import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data initializer to populate the database with default roles.
 * Runs on application startup.
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");

        try {
            // Initialize roles
            initializeRole("ADMINISTRADOR");
            initializeRole("OPERADOR");
            initializeRole("DOCENTE");
            initializeRole("ESTUDIANTE");

            log.info("Roles initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing roles: ", e);
            throw e;
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
