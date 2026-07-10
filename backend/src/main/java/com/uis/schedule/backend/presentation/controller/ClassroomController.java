package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.ClassroomNotFoundException;
import com.uis.schedule.backend.service.interfaces.ClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for classroom management operations.
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/classrooms")
@Tag(name = "Classrooms", description = "Classroom management endpoints")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @Operation(summary = "List active classrooms with pagination", description = "Retrieves a paginated list of active classrooms. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the active classrooms"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassroomListDTO>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassroomListDTO> classrooms = classroomService.listClassrooms(page, size);
        return ResponseEntity.ok(ApiResponse.success(classrooms, "Active classrooms retrieved successfully"));
    }

    @Operation(summary = "List all classrooms including inactive", description = "Retrieves a paginated list of all classrooms. Requires ADMINISTRADOR or OPERADOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all classrooms"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassroomListDTO>>> listAllWithInactive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassroomListDTO> classrooms = classroomService.listAllClassroomsIncludingInactive(page, size);
        return ResponseEntity.ok(ApiResponse.success(classrooms, "All classrooms retrieved successfully"));
    }

    @Operation(summary = "Get classrooms by status", description = "Retrieves a paginated list of classrooms filtered by status. Requires ADMINISTRADOR or OPERADOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved classrooms by status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassroomListDTO>>> getByStatus(
            @PathVariable boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassroomListDTO> classrooms = classroomService.findByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(classrooms, "Classrooms retrieved successfully by status"));
    }

    @Operation(summary = "Get classroom by ID", description = "Retrieves detailed information of a single classroom. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the classroom"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Classroom not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ClassroomDetailDTO>> getClassroomById(@PathVariable Long id) {
        ClassroomDetailDTO classroom = classroomService.findClassroomById(id)
                .orElseThrow(() -> new ClassroomNotFoundException(id));
        return ResponseEntity.ok(ApiResponse.success(classroom, "Classroom retrieved successfully"));
    }

    @Operation(summary = "Create a new classroom", description = "Creates a new classroom. Requires ADMINISTRADOR or OPERADOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Classroom created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid classroom data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> createClassroom(@Valid @RequestBody CreateClassroomRequest request) {
        ClassroomResponse createdClassroom = classroomService.createClassroom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdClassroom, "Classroom created successfully"));
    }

    @Operation(summary = "Update an existing classroom", description = "Updates an existing classroom. Requires ADMINISTRADOR or OPERADOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Classroom updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid classroom data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Classroom not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> updateClassroom(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClassroomRequest request) {
        ClassroomResponse updatedClassroom = classroomService.updateClassroom(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedClassroom, "Classroom updated successfully"));
    }

    @Operation(summary = "Delete a classroom", description = "Soft deletes a classroom by ID. Requires ADMINISTRADOR or OPERADOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Classroom deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Classroom not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERADOR')")
    public ResponseEntity<ApiResponse<Void>> deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Classroom deleted successfully (soft delete)"));
    }
}
