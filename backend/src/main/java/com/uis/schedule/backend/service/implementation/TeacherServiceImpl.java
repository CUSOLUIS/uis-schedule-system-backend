package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.TeacherEntity;
import com.uis.schedule.backend.persistence.repository.TeacherRepository;
import com.uis.schedule.backend.presentation.dto.CreateTeacherRequest;
import com.uis.schedule.backend.presentation.dto.PaginatedResponse;
import com.uis.schedule.backend.presentation.dto.TeacherDTO;
import com.uis.schedule.backend.presentation.dto.UpdateTeacherRequest;
import com.uis.schedule.backend.service.exception.TeacherNotFoundException;
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

    private final TeacherRepository teacherRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TeacherDTO> listTeachers(int page, int size) {
        Page<TeacherEntity> teachers = teacherRepository.findAll(PageRequest.of(page, size));
        return PaginatedResponse.<TeacherDTO>builder()
                .content(teachers.getContent().stream().map(this::toDto).toList())
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
                .map(this::toDto)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + teacherId));
    }

    @Override
    public TeacherDTO createTeacher(CreateTeacherRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Create teacher request cannot be null");
        }
        TeacherEntity saved = teacherRepository.save(TeacherEntity.builder()
                .fullName(request.getFullName().trim())
                .linkType(request.getLinkType().trim().toUpperCase())
                .availability(request.getAvailability())
                .department(request.getDepartment())
                .build());
        return toDto(saved);
    }

    @Override
    public TeacherDTO updateTeacher(UUID teacherId, UpdateTeacherRequest request) {
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Update teacher request cannot be null");
        }
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + teacherId));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            teacher.setFullName(request.getFullName().trim());
        }
        if (request.getLinkType() != null && !request.getLinkType().isBlank()) {
            teacher.setLinkType(request.getLinkType().trim().toUpperCase());
        }
        if (request.getAvailability() != null) {
            teacher.setAvailability(request.getAvailability());
        }
        if (request.getDepartment() != null) {
            teacher.setDepartment(request.getDepartment());
        }
        return toDto(teacherRepository.save(teacher));
    }

    private TeacherDTO toDto(TeacherEntity teacher) {
        return TeacherDTO.builder()
                .teacherId(teacher.getTeacherId())
                .fullName(teacher.getFullName())
                .linkType(teacher.getLinkType())
                .availability(teacher.getAvailability())
                .department(teacher.getDepartment())
                .build();
    }
}
