package com.uis.schedule.backend.service.implementation;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.presentation.dto.UserDTO;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.persistence.entity.UserEntity;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<UserDTO> listUsers(){
		return userRepository.findAll().stream()
			.map(this::toDTO)
			.collect(Collectors.toList());
	}

	public UserDTO findUserById(Long id){
		UserEntity userE = userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException(id));
		UserDTO user = this.toDTO(userE);
		return user;
	}

	private UserDTO toDTO (UserEntity user){
		UserDTO dto = new UserDTO();
		dto.setId(user.getUserId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		return dto;
	}
}
