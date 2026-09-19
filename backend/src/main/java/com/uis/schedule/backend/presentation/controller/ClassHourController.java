package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.ClassHourNotFoundException;
import com.uis.schedule.backend.service.interfaces.ClassHourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the active class hours",
                    content = @Content(schema = @Schema(implementation = ClassHourResponse.class),
                            examples = @ExampleObject(name = "Listado de horas de clase", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                            "startTime": "08:00:00",
                                            "endTime": "10:00:00",
                                            "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "dayNames": ["LUNES"],
                                            "startDate": "2026-07-16",
                                            "endDate": "2026-11-20",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Active class hours retrieved successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all class hours",
                    content = @Content(schema = @Schema(implementation = ClassHourResponse.class),
                            examples = @ExampleObject(name = "Listado de todas las horas de clase", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                            "startTime": "08:00:00",
                                            "endTime": "10:00:00",
                                            "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "dayNames": ["LUNES"],
                                            "startDate": "2026-07-16",
                                            "endDate": "2026-11-20",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "All class hours retrieved successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved class hours by status",
                    content = @Content(schema = @Schema(implementation = ClassHourResponse.class),
                            examples = @ExampleObject(name = "Horas de clase por estado", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                            "startTime": "08:00:00",
                                            "endTime": "10:00:00",
                                            "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "dayNames": ["LUNES"],
                                            "startDate": "2026-07-16",
                                            "endDate": "2026-11-20",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Class hours retrieved successfully by status",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have privileges",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the class hour",
                    content = @Content(schema = @Schema(implementation = ClassHourDetailDTO.class),
                            examples = @ExampleObject(name = "Detalle de hora de clase", value = """
                                    {
                                      "Data": {
                                        "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                        "startTime": "08:00:00",
                                        "endTime": "10:00:00",
                                        "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                        "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                        "dayNames": ["LUNES"],
                                        "startDate": "2026-07-16",
                                        "endDate": "2026-11-20",
                                        "isActive": true
                                      },
                                      "Message": "Class hour retrieved successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Class hour not found",
                    content = @Content(examples = @ExampleObject(name = "No encontrado", value = """
                            {
                              "Data": null,
                              "Message": "Class hour not found with ID: 1a2b3c4d-5e6f-4789-8abc-def012345679",
                              "Errors": ["Class hour not found with ID: 1a2b3c4d-5e6f-4789-8abc-def012345679"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved class hours by group",
                    content = @Content(schema = @Schema(implementation = ClassHourResponse.class),
                            examples = @ExampleObject(name = "Horas de clase por grupo", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                            "startTime": "08:00:00",
                                            "endTime": "10:00:00",
                                            "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "dayNames": ["LUNES"],
                                            "startDate": "2026-07-16",
                                            "endDate": "2026-11-20",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Class hours retrieved successfully by group",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved class hours by classroom",
                    content = @Content(schema = @Schema(implementation = ClassHourResponse.class),
                            examples = @ExampleObject(name = "Horas de clase por aula", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                            "startTime": "08:00:00",
                                            "endTime": "10:00:00",
                                            "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "dayNames": ["LUNES"],
                                            "startDate": "2026-07-16",
                                            "endDate": "2026-11-20",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Class hours retrieved successfully by classroom",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
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

    @Operation(summary = "Get class hours by day", description = "Retrieves a paginated list of class hours for a specific day of the week. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved class hours by day",
                    content = @Content(schema = @Schema(implementation = ClassHourResponse.class),
                            examples = @ExampleObject(name = "Horas de clase por día", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                            "startTime": "08:00:00",
                                            "endTime": "10:00:00",
                                            "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "dayNames": ["LUNES"],
                                            "startDate": "2026-07-16",
                                            "endDate": "2026-11-20",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Class hours retrieved successfully by day",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @GetMapping("/day/{dayId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassHourListDTO>>> getByDay(
            @PathVariable UUID dayId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassHourListDTO> classHours = classHourService.findByDayId(dayId, page, size);
        return ResponseEntity.ok(ApiResponse.success(classHours, "Class hours retrieved successfully by day"));
    }

    @Operation(summary = "Get class hours by teacher", description = "Retrieves a paginated list of class hours for a specific teacher. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved class hours by teacher",
                    content = @Content(schema = @Schema(implementation = ClassHourResponse.class),
                            examples = @ExampleObject(name = "Horas de clase por profesor", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                            "startTime": "08:00:00",
                                            "endTime": "10:00:00",
                                            "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "dayNames": ["LUNES"],
                                            "startDate": "2026-07-16",
                                            "endDate": "2026-11-20",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Class hours retrieved successfully by teacher",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassHourListDTO>>> getByTeacher(
            @PathVariable UUID teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassHourListDTO> classHours = classHourService.findByTeacherId(teacherId, page, size);
        return ResponseEntity.ok(ApiResponse.success(classHours, "Class hours retrieved successfully by teacher"));
    }

    @Operation(summary = "Create a new class hour", description = "Creates a new class hour. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Class hour created successfully",
                    content = @Content(schema = @Schema(implementation = ClassHourResponse.class),
                            examples = @ExampleObject(name = "Hora de clase creada", value = """
                                    {
                                      "Data": {
                                        "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                        "startTime": "08:00:00",
                                        "endTime": "10:00:00",
                                        "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                        "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                        "dayNames": ["LUNES"],
                                        "startDate": "2026-07-16",
                                        "endDate": "2026-11-20",
                                        "isActive": true
                                      },
                                      "Message": "Class hour created successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid class hour data or referenced entity not found/disabled",
                    content = @Content(examples = @ExampleObject(name = "Datos inválidos", value = """
                            {
                              "Data": null,
                              "Message": "Validation failed",
                              "Errors": ["startTime: must not be null"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Classroom or teacher is already occupied in the requested slot",
                    content = @Content(examples = @ExampleObject(name = "Conflicto de horario", value = """
                            {
                              "Data": null,
                              "Message": "The classroom is already occupied on the specified days and time range.",
                              "Errors": ["The classroom is already occupied on the specified days and time range."]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Class hour updated successfully",
                    content = @Content(schema = @Schema(implementation = ClassHourResponse.class),
                            examples = @ExampleObject(name = "Hora de clase actualizada", value = """
                                    {
                                      "Data": {
                                        "id": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                                        "startTime": "10:00:00",
                                        "endTime": "12:00:00",
                                        "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                        "classroomId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                        "dayNames": ["LUNES"],
                                        "startDate": "2026-07-16",
                                        "endDate": "2026-11-20",
                                        "isActive": true
                                      },
                                      "Message": "Class hour updated successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid class hour data or referenced entity not found/disabled",
                    content = @Content(examples = @ExampleObject(name = "Datos inválidos", value = """
                            {
                              "Data": null,
                              "Message": "End time must be after start time.",
                              "Errors": ["End time must be after start time."]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Class hour not found",
                    content = @Content(examples = @ExampleObject(name = "No encontrado", value = """
                            {
                              "Data": null,
                              "Message": "Class hour not found with ID: 1a2b3c4d-5e6f-4789-8abc-def012345679",
                              "Errors": ["Class hour not found with ID: 1a2b3c4d-5e6f-4789-8abc-def012345679"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Class hour deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "Hora de clase eliminada", value = """
                                    {
                                      "Data": null,
                                      "Message": "Class hour deleted successfully (soft delete)",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Class hour not found",
                    content = @Content(examples = @ExampleObject(name = "No encontrado", value = """
                            {
                              "Data": null,
                              "Message": "Class hour not found with ID: 1a2b3c4d-5e6f-4789-8abc-def012345679",
                              "Errors": ["Class hour not found with ID: 1a2b3c4d-5e6f-4789-8abc-def012345679"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteClassHour(@PathVariable UUID id) {
        classHourService.deleteClassHour(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Class hour deleted successfully (soft delete)"));
    }

    private static final String ERROR_401 = """
            {
              "Data": null,
              "Message": "Bad credentials",
              "Errors": ["Bad credentials"]
            }
            """;

    private static final String ERROR_403 = """
            {
              "Data": null,
              "Message": "You do not have sufficient permissions to perform this action",
              "Errors": ["You do not have sufficient permissions to perform this action"]
            }
            """;

    private static final String ERROR_500 = """
            {
              "Data": null,
              "Message": "An internal server error occurred",
              "Errors": ["An internal server error occurred"]
            }
            """;
}
