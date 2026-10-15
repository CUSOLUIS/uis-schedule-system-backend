package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.TeacherDTO;
import com.uis.schedule.backend.service.exception.TeacherNotFoundException;
import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.service.interfaces.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final TeacherProfileService teacherProfileService;

    @Override
    @Transactional
    public TeacherDTO getCurrentTeacher(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new UserNotFoundException("Authenticated user not found");
        }

        UserEntity user = userRepository.findUserEntityByEmailOrUsername(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

        if (!teacherProfileService.hasTeacherRole(user)) {
            throw new TeacherNotFoundException("The authenticated user is not linked to a teacher profile.");
        }

        TeacherEntity teacher = teacherProfileService.ensureFor(user);
        if (teacher == null) {
            throw new TeacherNotFoundException("The authenticated user is not linked to a teacher profile.");
        }
        return teacherProfileService.toDto(teacher);
    }
}
