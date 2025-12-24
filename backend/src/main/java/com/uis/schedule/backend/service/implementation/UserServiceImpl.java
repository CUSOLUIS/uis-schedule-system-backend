package com.uis.schedule.backend.service.implementation;

import java.util.List;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.presentation.dto.AuthLoginRequest;
import com.uis.schedule.backend.presentation.dto.AuthResponse;
import com.uis.schedule.backend.util.mapper.UserMapper;


import lombok.extern.slf4j.Slf4j;

import com.uis.schedule.backend.presentation.dto.AuthSignupRequest;
import com.uis.schedule.backend.presentation.dto.UserDTO;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.configuration.jwt.JwtUtil;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.service.exception.UserNotFoundException;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> findUserById(Long id) {
        Optional<UserDTO> user = userRepository.findById(id)
                .map(UserMapper::entityToDTO);
        return user;
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        UserEntity entity = UserMapper.dtoToEntity(user);
        try {
            UserEntity entitySaved = userRepository.save(entity);
            return UserMapper.entityToDTO(entitySaved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User creation failed: The email '" + user.getEmail() + "' may already be in use.");
        }
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO user) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Update fields from DTO
        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());
        // Note: Password and roles are not updated here for security reasons.
        // They should have their own dedicated methods if needed.

        try {
            UserEntity updatedUser = userRepository.save(userToUpdate);
            return UserMapper.entityToDTO(updatedUser);
        } catch (DataIntegrityViolationException e) {
            // This is likely due to the email already existing for another user.
            throw new IllegalArgumentException("Email '" + user.getEmail() + "' is already in use by another user.");
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public AuthResponse signUp(AuthSignupRequest authSignupRequest) {
        log.info("Registro interno de un usuario {}.", authSignupRequest.email());
        try {
            UserEntity user = userRepository.findUserEntityByEmailOrName(authSignupRequest.email(), authSignupRequest.username()).orElse(null);
            if (Objects.isNull(user)) {
                UserEntity newUser = new UserEntity();
                newUser.setName(authSignupRequest.username());
                newUser.setEmail(authSignupRequest.email());
                String encodedPassword = passwordEncoder.encode(authSignupRequest.password());
                log.info("Encoded password: {}", encodedPassword);
                newUser.setPassword(encodedPassword);
                newUser.setRole("user");
                userRepository.save(newUser);
                return new AuthResponse(authSignupRequest.username(), "User registered successfully", null, true);
            } else {
                return new AuthResponse(authSignupRequest.username(), "Email already registered", null, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new AuthResponse(null, "Something went wrong", null, false);
    }

    @Override
    public AuthResponse login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email, password
                    )
            );
            if (authentication.isAuthenticated()) {
                String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                UserEntity user = userRepository.findUserEntityByEmailOrName(username, username)
                        .orElseThrow(() -> new UserNotFoundException("Authenticated user not found in database: " + username));

                String token = jwtUtil.generateToken(
                        user.getUserId(),
                        user.getEmail(),
                        user.getRoles().iterator().next().getRoleEnum().name());
                
                return new AuthResponse(user.getName(), "Login successful", token, true);
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return new AuthResponse(null, "Bad credentials", null, false);
    }
}