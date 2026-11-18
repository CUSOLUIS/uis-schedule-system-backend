package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.presentation.dto.CreateTeacherRequest;
import com.uis.schedule.backend.presentation.dto.PaginatedResponse;
import com.uis.schedule.backend.presentation.dto.TeacherDTO;
import com.uis.schedule.backend.presentation.dto.UpdateTeacherRequest;
import com.uis.schedule.backend.service.interfaces.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Academic teacher catalog endpoints (no login / portal).
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/teachers")
@Tag(name = "Teachers", description = "Teacher catalog endpoints")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @Operation(summary = "List teachers",
            description = "Paginated teacher catalog for scheduling. Requires ADMINISTRATOR.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Teachers retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<PaginatedResponse<TeacherDTO>>> listTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<TeacherDTO> teachers = teacherService.listTeachers(page, size);
        return ResponseEntity.ok(ApiResponse.success(teachers, "Teachers retrieved successfully"));
    }

    @Operation(summary = "Get a teacher by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<TeacherDTO>> getTeacherById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                teacherService.findTeacherById(id), "Teacher retrieved successfully"));
    }

    @Operation(summary = "Create a teacher in the catalog")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<TeacherDTO>> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(teacherService.createTeacher(request), "Teacher created successfully"));
    }

    @Operation(summary = "Update a teacher in the catalog")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<TeacherDTO>> updateTeacher(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeacherRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                teacherService.updateTeacher(id, request), "Teacher updated successfully"));
    }
}
