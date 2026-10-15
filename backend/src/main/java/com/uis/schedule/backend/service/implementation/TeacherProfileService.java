package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.TeacherRepository;
import com.uis.schedule.backend.presentation.dto.TeacherDTO;
import com.uis.schedule.backend.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Keeps a {@link TeacherEntity} in sync with user accounts that have the TEACHER role,
 * so schedule queries can resolve a teacherId from the authenticated user.
 */
@Service
@RequiredArgsConstructor
public class TeacherProfileService {

    public static final String TEACHER_ROLE = "TEACHER";

    private final TeacherRepository teacherRepository;

    @Transactional
    public TeacherEntity ensureFor(UserEntity user) {
        if (user == null || user.getUserId() == null || !hasTeacherRole(user)) {
            return null;
        }

        return teacherRepository.findByUserId_UserId(user.getUserId())
                .orElseGet(() -> teacherRepository.save(TeacherEntity.builder()
                        .userId(user)
                        .build()));
    }

    public TeacherDTO toDto(TeacherEntity teacher) {
        if (teacher == null) {
            return null;
        }
        return new TeacherDTO(
                teacher.getTeacherId(),
                teacher.getAvailability(),
                teacher.getDepartment(),
                UserMapper.entityToListDTO(teacher.getUserId()));
    }

    public boolean hasTeacherRole(UserEntity user) {
        if (user == null || user.getRoles() == null) {
            return false;
        }
        return user.getRoles().stream()
                .map(RoleEntity::getName)
                .anyMatch(name -> name != null && TEACHER_ROLE.equalsIgnoreCase(name));
    }
}
