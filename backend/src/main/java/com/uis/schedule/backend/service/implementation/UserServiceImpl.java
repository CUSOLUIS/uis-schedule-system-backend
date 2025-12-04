package com.uis.schedule.backend.service.implementation;

import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Service;

import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.presentation.dto.AuthLoginRequest;
import com.uis.schedule.backend.util.mapper.UserMapper;
import com.uis.schedule.backend.util.RequestResponseUtils;

import lombok.extern.slf4j.Slf4j;

import com.uis.schedule.backend.presentation.dto.UserDTO;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.configuration.jwt.JwtUtil;
import com.uis.schedule.backend.persistence.entity.UserEntity;

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

    public UserDTO createUser(UserDTO user) {
        UserEntity entity = UserMapper.dtoToEntity(user);
        UserEntity entitySaved = userRepository.save(entity);
        return UserMapper.entityToDTO(entitySaved);
    }

    public UserDTO updateUser(UserDTO user) {
        UserEntity entity = UserMapper.dtoToEntity(user);
        UserEntity entitySaved = userRepository.save(entity);
        return UserMapper.entityToDTO(entitySaved);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Registro interno de un usuario {}.", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                UserEntity user = userRepository.findUserEntityByEmail(requestMap.get("email")).orElse(null);
                if (Objects.isNull(user)) {
                    userRepository.save(getUserFromMap(requestMap));
                    return RequestResponseUtils.getResponseEntity("Registro exitoso", HttpStatus.CREATED);
                } else {
                    return RequestResponseUtils.getResponseEntity("El correo electrónico ya está registrado",
                            HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RequestResponseUtils.getResponseEntity("Algo salió mal", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("username") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("password");
    }

    private UserEntity getUserFromMap(Map<String, String> requestMap) {
        UserEntity user = new UserEntity();
        user.setName(requestMap.get("username"));
        user.setEmail(requestMap.get("email"));
        String encodedPassword = passwordEncoder.encode(requestMap.get("password"));
        log.info("Encoded password: {}", encodedPassword);
        user.setPassword(encodedPassword);
        user.setEnable(true);
        user.setAccountNoExpired(true);
        user.setAccountNoLocked(true);
        user.setCredentialNoExpired(true);
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> login(AuthLoginRequest request) {
        log.info("Inicio de sesión interno de un usuario {}.");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(), request.password()));
            if (authentication.isAuthenticated()) {
                String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
                        .getUsername();
                UserEntity user = userRepository.findUserEntityByEmail(username).orElse(null);

                if (user.isEnable()) {
                    return new ResponseEntity<String>(
                            "{\"token\":\"" + jwtUtil.generateToken(
                                    user.getEmail(),
                                    user.getRoles().iterator().next().getRoleEnum().name()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"mensaje\":\"Usuario no aprobado por el administrador\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return new ResponseEntity<String>("{\"mensaje\":\"Credenciales incorrectas\"}", HttpStatus.BAD_REQUEST);
    }
}