package com.uis.schedule.backend.service.implementation;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.util.mapper.UserMapper;
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
			.map(UserMapper::entityToDTO)
			.collect(Collectors.toList());
	}

	public Optional<UserDTO> findUserById(Long id){
		Optional<UserDTO> user = userRepository.findById(id)
			.map(UserMapper::entityToDTO);
		return user;
	}

	public UserDTO createUser(UserDTO user){
		UserEntity entity = UserMapper.dtoToEntity(user);
		UserEntity entitySaved = userRepository.save(entity);
		return UserMapper.entityToDTO(entitySaved);
	}

	public UserDTO updateUser(UserDTO user){
		UserEntity entity = UserMapper.dtoToEntity(user);
		UserEntity entitySaved = userRepository.save(entity);
		return UserMapper.entityToDTO(entitySaved);
	}
	public void deleteUser(Long id){
		userRepository.deleteById(id);
	}
}
