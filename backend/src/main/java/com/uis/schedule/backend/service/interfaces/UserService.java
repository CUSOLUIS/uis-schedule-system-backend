package com.uis.schedule.backend.service.interfaces;

import java.util.List;
import java.util.Optional;
import com.uis.schedule.backend.presentation.dto.UserDTO;
import com.uis.schedule.backend.presentation.dto.AuthSignupRequest;

import com.uis.schedule.backend.presentation.dto.AuthLoginRequest;
import com.uis.schedule.backend.presentation.dto.AuthResponse;

public interface UserService {
	List<UserDTO> listUsers();

	Optional<UserDTO> findUserById(Long id);

	UserDTO createUser(UserDTO user);

	UserDTO updateUser(UserDTO user);

	void deleteUser(Long id);

		AuthResponse signUp(AuthSignupRequest authSignupRequest);

		AuthResponse login(String email, String password);
}
