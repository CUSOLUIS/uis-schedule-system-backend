package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.TeacherRepository;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import com.uis.schedule.backend.presentation.dto.PaginatedResponse;
import com.uis.schedule.backend.presentation.dto.TeacherDTO;
import com.uis.schedule.backend.service.exception.TeacherNotFoundException;
import com.uis.schedule.backend.service.exception.UserNotFoundException;
import com.uis.schedule.backend.service.interfaces.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherProfileService teacherProfileService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TeacherDTO> listTeachers(int page, int size) {
        Page<TeacherEntity> teachers = teacherRepository.findAll(PageRequest.of(page, size));
        return PaginatedResponse.<TeacherDTO>builder()
                .content(teachers.getContent().stream().map(teacherProfileService::toDto).toList())
                .pageNumber(teachers.getNumber())
                .pageSize(teachers.getSize())
                .totalElements(teachers.getTotalElements())
                .totalPages(teachers.getTotalPages())
                .last(teachers.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherDTO findTeacherById(UUID teacherId) {
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }
        return teacherRepository.findById(teacherId)
                .map(teacherProfileService::toDto)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + teacherId));
    }

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
