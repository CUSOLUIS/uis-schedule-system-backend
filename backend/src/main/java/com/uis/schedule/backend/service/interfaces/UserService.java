package com.uis.schedule.backend.service.interfaces;

import java.util.List;
import java.util.Optional;
import com.uis.schedule.backend.presentation.dto.UserDTO;
import com.uis.schedule.backend.presentation.dto.AuthSignupRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {
	List<UserDTO> listUsers();
	Optional<UserDTO> findUserById(Long id);
	UserDTO createUser(UserDTO user);
	UserDTO updateUser(UserDTO user);
	void deleteUser(Long id);

	ResponseEntity<String> signUp(AuthSignupRequest authSignupRequest);

	ResponseEntity<String> login(String email, String password);
}
