package com.uis.schedule.backend.service.implementation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
    public PaginatedResponse<UserListDTO> listUsers(int page, int size) {
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
                    size);
            org.springframework.data.domain.Page<UserEntity> usersPage = userRepository.findAllByIsEnableTrue(pageable);

            return convertToPaginatedResponse(usersPage);
        } catch (Exception e) {
            log.error("Error retrieving active users list", e);
            return new PaginatedResponse<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<UserListDTO> listAllUsersIncludingInactive(int page, int size) {
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
                    size);
            org.springframework.data.domain.Page<UserEntity> usersPage = userRepository.findAll(pageable);

            return convertToPaginatedResponse(usersPage);
        } catch (Exception e) {
            log.error("Error retrieving all users list", e);
            return new PaginatedResponse<>();
        }
    }

    private PaginatedResponse<UserListDTO> convertToPaginatedResponse(
            org.springframework.data.domain.Page<UserEntity> usersPage) {
        List<UserListDTO> content = usersPage.getContent().stream()
                .map(UserMapper::entityToListDTO)
                .collect(Collectors.toList());

        return PaginatedResponse.<UserListDTO>builder()
                .content(content)
                .pageNumber(usersPage.getNumber())
                .pageSize(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .last(usersPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<UserDetailDTO> findUserById(UUID id) {
        if (id == null) {
            log.warn("Attempted to find user with null ID");
            return java.util.Optional.empty();
        }

        return userRepository.findByUserIdAndIsEnableTrue(id)
                .map(UserMapper::entityToDetailDTO);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        int min = 1;
        int max = 1000;
        // Formula: (int) (Math.random() * (max - min + 1) + min)
        int randomInt = (int) (Math.random() * (max - min + 1) + min);

        if (request == null) {
            throw new IllegalArgumentException("Create user request cannot be null");
        }

        log.info("Creating new user with email: {}", request.getEmail());

        try {
            String email = request.getEmail().toLowerCase();
            String username = (request.getFirstName().substring(0, 1) + request.getLastName() + randomInt)
                    .toLowerCase();

            UserEntity entity = UserEntity.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .username(username)
                    .email(email)
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
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
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

        if (request.getEmail() != null) {
            request.setEmail(request.getEmail().toLowerCase());
        }

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
    public void deleteUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        log.info("Soft deleting user with ID: {}", id);

        UserEntity user = userRepository.findByUserIdAndIsEnableTrue(id)
                .orElseThrow(() -> {
                    log.error("User not found or already disabled with ID: {}", id);
                    return new UserNotFoundException("User not found or already disabled with id: " + id);
                });

        user.setEnable(false);
        userRepository.save(user);
        
        log.info("User soft-deleted successfully with ID: {}", id);
    }

    @Override
    public AuthResponse signUp(AuthSignupRequest authSignupRequest) {
        String email = authSignupRequest.email().toLowerCase();
        log.info("Registro interno de un usuario {}.", email);

        try {
            String username = (authSignupRequest.firstName().substring(0, 1) + authSignupRequest.lastName())
                    .toLowerCase();

            UserEntity user = userRepository
                    .findUserEntityByEmailOrUsername(email, username)
                    .orElse(null);

            if (Objects.isNull(user)) {
                UserEntity newUser = new UserEntity();
                newUser.setFirstName(authSignupRequest.firstName());
                newUser.setLastName(authSignupRequest.lastName());
                newUser.setUsername(username);
                newUser.setEmail(email);
                String encodedPassword = passwordEncoder.encode(authSignupRequest.password());
                log.info("Encoded password: {}", encodedPassword);
                newUser.setPassword(encodedPassword);
                newUser.setEnable(true);
                newUser.setAccountNoExpired(true);
                newUser.setAccountNoLocked(true);
                newUser.setCredentialNoExpired(true);

                userRepository.save(newUser);
                log.info("User registered successfully: {}", authSignupRequest.email());

                // No roles assigned yet in this simplified signup, but let's assume default or handle null
                String role = (newUser.getRoles() != null && !newUser.getRoles().isEmpty())
                        ? newUser.getRoles().iterator().next().getName()
                        : "USER"; // No prefix here

                String token = jwtUtil.generateToken(
                        newUser.getUserId(),
                        newUser.getUsername(),
                        newUser.getEmail(),
                        role,
                        newUser.isEnable());

                return new AuthResponse(token, "User registered successfully");
            } else {
                log.warn("Email already registered or username taken: {}", authSignupRequest.email());
                return new AuthResponse(null, "Email already registered or username taken");
            }
        } catch (Exception ex) {
            log.error("Error during user signup", ex);
            return new AuthResponse(null, "Something went wrong");
        }
    }

    @Override
    public AuthResponse login(String email, String password) {
        String normalizedEmail = email.toLowerCase();
        log.info("Login attempt for email: {}", normalizedEmail);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, password));

            if (authentication.isAuthenticated()) {
                String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
                        .getUsername();

                UserEntity user = userRepository.findUserEntityByEmailOrUsername(username, username)
                        .orElseThrow(() -> {
                            log.error("Authenticated user not found in database: {}", username);
                            return new UserNotFoundException("Authenticated user not found in database: " + username);
                        });

                String role = "USER";
                if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                    role = user.getRoles().iterator().next().getName();
                }

                String token = jwtUtil.generateToken(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        role,
                        user.isEnable());

                log.info("Login successful for user: {} with role: {}", username, role);
                return new AuthResponse(token, "Login successful");
            }
        } catch (Exception e) {
            log.error("Login failed for email: {}", email, e);
        }

        return new AuthResponse(null, "Bad credentials");
    }
}