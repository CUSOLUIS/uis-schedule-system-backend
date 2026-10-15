package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.presentation.dto.TeacherDTO;
import com.uis.schedule.backend.service.interfaces.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Teacher profile endpoints. {@code GET /api/teachers/me} resolves the teacherId
 * required by {@code GET /api/class-hours/teacher/{teacherId}}.
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/teachers")
@Tag(name = "Teachers", description = "Teacher profile endpoints")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @Operation(summary = "Get the authenticated teacher's profile",
            description = "Returns the TeacherEntity linked to the current user. Requires the TEACHER role. "
                    + "If the account has the role but no profile yet, the profile is created.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Teacher profile retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Authenticated user is not a teacher")
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherDTO>> getCurrentTeacher(Authentication authentication) {
        TeacherDTO teacher = teacherService.getCurrentTeacher(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(teacher, "Teacher profile retrieved successfully"));
    }
}
