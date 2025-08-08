package com.uis.schedule.backend.service.interfaces;

import java.util.List;
import com.uis.schedule.backend.presentation.dto.UserDTO;

public interface UserService {
	List<UserDTO> listUsers();
	UserDTO findUserById(Long id);
}
