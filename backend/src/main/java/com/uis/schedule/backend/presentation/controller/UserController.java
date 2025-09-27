package com.uis.schedule.backend.presentation.controller;


import java.util.List;
import java.util.Optional;

//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.service.interfaces.UserService;
import com.uis.schedule.backend.presentation.dto.UserDTO;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService){
		this.userService = userService;
	}

	@GetMapping("")
	public List<UserDTO> listAll() {
		return userService.listUsers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
		return userService.findUserById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user){
		return new ResponseEntity<>(userService.createUser(user),HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO user){
		return ResponseEntity.ok(userService.updateUser(user));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id){
		return userService.findUserById(id)
			.map(c -> {
				userService.deleteUser(id);
				return ResponseEntity.ok(c);
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
