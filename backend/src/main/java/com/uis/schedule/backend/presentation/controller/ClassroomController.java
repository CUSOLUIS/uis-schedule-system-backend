package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.ClassroomNotFoundException;
import com.uis.schedule.backend.service.interfaces.ClassroomService;
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the active classrooms",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class),
                            examples = @ExampleObject(name = "Listado de aulas", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "number": "301",
                                            "maxCapacity": 40,
                                            "building": "Ciencias",
                                            "campus": "Principal",
                                            "type": "AULA",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Active classrooms retrieved successfully",
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
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassroomListDTO>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassroomListDTO> classrooms = classroomService.listClassrooms(page, size);
        return ResponseEntity.ok(ApiResponse.success(classrooms, "Active classrooms retrieved successfully"));
    }

    @Operation(summary = "List all classrooms including inactive", description = "Retrieves a paginated list of all classrooms. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all classrooms",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class),
                            examples = @ExampleObject(name = "Listado de todas las aulas", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "number": "301",
                                            "maxCapacity": 40,
                                            "building": "Ciencias",
                                            "campus": "Principal",
                                            "type": "AULA",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "All classrooms retrieved successfully",
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
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassroomListDTO>>> listAllWithInactive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassroomListDTO> classrooms = classroomService.listAllClassroomsIncludingInactive(page, size);
        return ResponseEntity.ok(ApiResponse.success(classrooms, "All classrooms retrieved successfully"));
    }

    @Operation(summary = "Get classrooms by status", description = "Retrieves a paginated list of classrooms filtered by status. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved classrooms by status",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class),
                            examples = @ExampleObject(name = "Aulas por estado", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "number": "301",
                                            "maxCapacity": 40,
                                            "building": "Ciencias",
                                            "campus": "Principal",
                                            "type": "AULA",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Classrooms retrieved successfully by status",
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
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassroomListDTO>>> getByStatus(
            @PathVariable boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassroomListDTO> classrooms = classroomService.findByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(classrooms, "Classrooms retrieved successfully by status"));
    }

    @Operation(summary = "Search classrooms", description = "Searches active classrooms by name (number), campus (sede), building and minimum capacity. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved matching classrooms",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class),
                            examples = @ExampleObject(name = "Búsqueda de aulas", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                            "number": "301",
                                            "maxCapacity": 40,
                                            "building": "Ciencias",
                                            "campus": "Principal",
                                            "type": "AULA",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Classrooms retrieved successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClassroomListDTO>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String campus,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<ClassroomListDTO> classrooms = classroomService.searchClassrooms(name, campus, building, capacity, page, size);
        return ResponseEntity.ok(ApiResponse.success(classrooms, "Classrooms retrieved successfully"));
    }

    @Operation(summary = "Get classroom by ID", description = "Retrieves detailed information of a single classroom. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the classroom",
                    content = @Content(schema = @Schema(implementation = ClassroomDetailDTO.class),
                            examples = @ExampleObject(name = "Detalle de aula", value = """
                                    {
                                      "Data": {
                                        "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                        "number": "301",
                                        "maxCapacity": 40,
                                        "building": "Ciencias",
                                        "campus": "Principal",
                                        "type": "AULA",
                                        "isActive": true
                                      },
                                      "Message": "Classroom retrieved successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Classroom not found",
                    content = @Content(examples = @ExampleObject(name = "No encontrado", value = """
                            {
                              "Data": null,
                              "Message": "Classroom not found with ID: 7c9e6679-7425-40de-944b-e07fc1f90ae7",
                              "Errors": ["Classroom not found with ID: 7c9e6679-7425-40de-944b-e07fc1f90ae7"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ClassroomDetailDTO>> getClassroomById(@PathVariable UUID id) {
        ClassroomDetailDTO classroom = classroomService.findClassroomById(id)
                .orElseThrow(() -> new ClassroomNotFoundException(id));
        return ResponseEntity.ok(ApiResponse.success(classroom, "Classroom retrieved successfully"));
    }

    @Operation(summary = "Create a new classroom", description = "Creates a new classroom. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Classroom created successfully",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class),
                            examples = @ExampleObject(name = "Aula creada", value = """
                                    {
                                      "Data": {
                                        "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                        "number": "301",
                                        "maxCapacity": 40,
                                        "building": "Ciencias",
                                        "campus": "Principal",
                                        "type": "AULA",
                                        "isActive": true
                                      },
                                      "Message": "Classroom created successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid classroom data",
                    content = @Content(examples = @ExampleObject(name = "Datos inválidos", value = """
                            {
                              "Data": null,
                              "Message": "Validation failed",
                              "Errors": ["number: must not be blank"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "A classroom with the same number, campus and building already exists",
                    content = @Content(examples = @ExampleObject(name = "Conflicto", value = """
                            {
                              "Data": null,
                              "Message": "A classroom with the same number, campus and building already exists",
                              "Errors": ["A classroom with the same number, campus and building already exists"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> createClassroom(@Valid @RequestBody CreateClassroomRequest request) {
        ClassroomResponse createdClassroom = classroomService.createClassroom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdClassroom, "Classroom created successfully"));
    }

    @Operation(summary = "Update an existing classroom", description = "Updates an existing classroom. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Classroom updated successfully",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class),
                            examples = @ExampleObject(name = "Aula actualizada", value = """
                                    {
                                      "Data": {
                                        "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                        "number": "301",
                                        "maxCapacity": 45,
                                        "building": "Ciencias",
                                        "campus": "Principal",
                                        "type": "AULA",
                                        "isActive": true
                                      },
                                      "Message": "Classroom updated successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid classroom data or number already exists",
                    content = @Content(examples = @ExampleObject(name = "Datos inválidos", value = """
                            {
                              "Data": null,
                              "Message": "Validation failed",
                              "Errors": ["maxCapacity: must be greater than 0"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Classroom not found",
                    content = @Content(examples = @ExampleObject(name = "No encontrado", value = """
                            {
                              "Data": null,
                              "Message": "Classroom not found with ID: 7c9e6679-7425-40de-944b-e07fc1f90ae7",
                              "Errors": ["Classroom not found with ID: 7c9e6679-7425-40de-944b-e07fc1f90ae7"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> updateClassroom(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClassroomRequest request) {
        ClassroomResponse updatedClassroom = classroomService.updateClassroom(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedClassroom, "Classroom updated successfully"));
    }

    @Operation(summary = "Delete a classroom", description = "Soft deletes a classroom by ID. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Classroom deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "Aula eliminada", value = """
                                    {
                                      "Data": null,
                                      "Message": "Classroom deleted successfully (soft delete)",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Classroom not found",
                    content = @Content(examples = @ExampleObject(name = "No encontrado", value = """
                            {
                              "Data": null,
                              "Message": "Classroom not found with ID: 7c9e6679-7425-40de-944b-e07fc1f90ae7",
                              "Errors": ["Classroom not found with ID: 7c9e6679-7425-40de-944b-e07fc1f90ae7"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Classroom still has active groups assigned",
                    content = @Content(examples = @ExampleObject(name = "Conflicto", value = """
                            {
                              "Data": null,
                              "Message": "Classroom still has active groups assigned",
                              "Errors": ["Classroom still has active groups assigned"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteClassroom(@PathVariable UUID id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Classroom deleted successfully (soft delete)"));
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
