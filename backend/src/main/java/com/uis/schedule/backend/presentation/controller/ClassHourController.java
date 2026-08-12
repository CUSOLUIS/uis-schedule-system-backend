package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.ClassHourNotFoundException;
import com.uis.schedule.backend.service.interfaces.ClassHourService;
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
 * REST Controller for class hour management operations.
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/class-hours")
@Tag(name = "Class Hours", description = "Class hour management endpoints")
public class ClassHourController {

    private final ClassHourService classHourService;

    public ClassHourController(ClassHourService classHourService) {
        this.classHourService = classHourService;
    }

    @Operation(summary = "List active class hours with pagination", description = "Retrieves a paginated list of active class hours. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the active class hours"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassHourListDTO>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassHourListDTO> classHours = classHourService.listClassHours(page, size);
        return ResponseEntity.ok(ApiResponse.success(classHours, "Active class hours retrieved successfully"));
    }

    @Operation(summary = "List all class hours including inactive", description = "Retrieves a paginated list of all class hours. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all class hours"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassHourListDTO>>> listAllWithInactive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassHourListDTO> classHours = classHourService.listAllClassHoursIncludingInactive(page, size);
        return ResponseEntity.ok(ApiResponse.success(classHours, "All class hours retrieved successfully"));
    }

    @Operation(summary = "Get class hours by status", description = "Retrieves a paginated list of class hours filtered by status. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved class hours by status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassHourListDTO>>> getByStatus(
            @PathVariable boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassHourListDTO> classHours = classHourService.findByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(classHours, "Class hours retrieved successfully by status"));
    }

    @Operation(summary = "Get class hour by ID", description = "Retrieves detailed information of a single class hour. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the class hour"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Class hour not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ClassHourDetailDTO>> getClassHourById(@PathVariable UUID id) {
        ClassHourDetailDTO classHour = classHourService.findClassHourById(id)
                .orElseThrow(() -> new ClassHourNotFoundException(id));
        return ResponseEntity.ok(ApiResponse.success(classHour, "Class hour retrieved successfully"));
    }

    @Operation(summary = "Get class hours by group", description = "Retrieves a paginated list of class hours for a specific group. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved class hours by group"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/group/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassHourListDTO>>> getByGroup(
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassHourListDTO> classHours = classHourService.findByGroupId(groupId, page, size);
        return ResponseEntity.ok(ApiResponse.success(classHours, "Class hours retrieved successfully by group"));
    }

    @Operation(summary = "Get class hours by classroom", description = "Retrieves a paginated list of class hours for a specific classroom. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved class hours by classroom"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/classroom/{classroomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassHourListDTO>>> getByClassroom(
            @PathVariable UUID classroomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassHourListDTO> classHours = classHourService.findByClassroomId(classroomId, page, size);
        return ResponseEntity.ok(ApiResponse.success(classHours, "Class hours retrieved successfully by classroom"));
    }

    @Operation(summary = "Create a new class hour", description = "Creates a new class hour. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Class hour created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid class hour data or referenced entity not found/disabled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<ClassHourResponse>> createClassHour(@Valid @RequestBody CreateClassHourRequest request) {
        ClassHourResponse createdClassHour = classHourService.createClassHour(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdClassHour, "Class hour created successfully"));
    }

    @Operation(summary = "Update an existing class hour", description = "Updates an existing class hour. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Class hour updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid class hour data or referenced entity not found/disabled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Class hour not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<ClassHourResponse>> updateClassHour(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClassHourRequest request) {
        ClassHourResponse updatedClassHour = classHourService.updateClassHour(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedClassHour, "Class hour updated successfully"));
    }

    @Operation(summary = "Delete a class hour", description = "Soft deletes a class hour by ID. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Class hour deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Class hour not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteClassHour(@PathVariable UUID id) {
        classHourService.deleteClassHour(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Class hour deleted successfully (soft delete)"));
    }
}
