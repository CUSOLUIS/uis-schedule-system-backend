package com.uis.schedule.backend.service.implementation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uis.schedule.backend.configuration.jwt.JwtUtil;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.util.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of UserService interface.
 * Handles all user-related business logic following best practices.
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserListDTO> listUsers() {
        try {
            List<UserEntity> users = userRepository.findAll();

            if (users.isEmpty()) {
                log.info("No users found in database");
                return Collections.emptyList();
            }

            return users.stream()
                    .map(UserMapper::entityToListDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving users list", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<UserDetailDTO> findUserById(Long id) {
        if (id == null) {
            log.warn("Attempted to find user with null ID");
            return java.util.Optional.empty();
        }

        return userRepository.findById(id)
                .map(UserMapper::entityToDetailDTO);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Create user request cannot be null");
        }

        log.info("Creating new user with email: {}", request.getEmail());


        try {
            UserEntity entity = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isEnable(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();
            UserEntity savedEntity = userRepository.save(entity);

            log.info("User created successfully with ID: {}", savedEntity.getUserId());
            return UserMapper.entityToResponse(savedEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating user: {}", request.getEmail(), e);
            throw new IllegalArgumentException(
                    "User creation failed: The email '" + request.getEmail() + "' may already be in use.");
        } catch (Exception e) {
            log.error("Unexpected error while creating user: {}", request.getEmail(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Update user request cannot be null");
        }

        log.info("Updating user with ID: {}", id);

        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });

        try {
            UserMapper.updateEntityFromRequest(userToUpdate, request);
            UserEntity updatedUser = userRepository.save(userToUpdate);

            log.info("User updated successfully with ID: {}", id);
            return UserMapper.entityToResponse(updatedUser);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating user: {}", id, e);
            throw new IllegalArgumentException(
                    "Email '" + request.getEmail() + "' is already in use by another user.");
        } catch (Exception e) {
            log.error("Unexpected error while updating user: {}", id, e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            log.error("User not found with ID: {}", id);
            throw new UserNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    @Override
    public AuthResponse signUp(AuthSignupRequest authSignupRequest) {
        log.info("Registro interno de un usuario {}.", authSignupRequest.email());

        try {
            UserEntity user = userRepository
                    .findUserEntityByEmailOrName(authSignupRequest.email(), authSignupRequest.username())
                    .orElse(null);

            if (Objects.isNull(user)) {
                UserEntity newUser = new UserEntity();
                newUser.setName(authSignupRequest.username());
                newUser.setEmail(authSignupRequest.email());
                String encodedPassword = passwordEncoder.encode(authSignupRequest.password());
                log.info("Encoded password: {}", encodedPassword);
                newUser.setPassword(encodedPassword);
                newUser.setEnable(true);
                newUser.setAccountNoExpired(true);
                newUser.setAccountNoLocked(true);
                newUser.setCredentialNoExpired(true);

                userRepository.save(newUser);
                log.info("User registered successfully: {}", authSignupRequest.email());
                return new AuthResponse(authSignupRequest.username(), "User registered successfully", null, true);
            } else {
                log.warn("Email already registered: {}", authSignupRequest.email());
                return new AuthResponse(authSignupRequest.username(), "Email already registered", null, false);
            }
        } catch (Exception ex) {
            log.error("Error during user signup", ex);
            return new AuthResponse(null, "Something went wrong", null, false);
        }
    }

    @Override
    public AuthResponse login(String email, String password) {
        log.info("Login attempt for email: {}", email);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            if (authentication.isAuthenticated()) {
                String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
                        .getUsername();

                UserEntity user = userRepository.findUserEntityByEmailOrName(username, username)
                        .orElseThrow(() -> {
                            log.error("Authenticated user not found in database: {}", username);
                            return new UserNotFoundException("Authenticated user not found in database: " + username);
                        });

                String token = jwtUtil.generateToken(
                        user.getUserId(),
                        user.getEmail(),
                        user.getRoles().iterator().next().getRoleEnum().name());

                log.info("Login successful for user: {}", username);
                return new AuthResponse(user.getName(), "Login successful", token, true);
            }
        } catch (Exception e) {
            log.error("Login failed for email: {}", email, e);
        }

        return new AuthResponse(null, "Bad credentials", null, false);
    }
}