package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.exception.GroupNotFoundException;
import com.uis.schedule.backend.service.interfaces.GroupService;
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
 * REST Controller for group management operations.
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/groups")
@Tag(name = "Groups", description = "Group management endpoints")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(summary = "List active groups with pagination", description = "Retrieves a paginated list of active groups. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the active groups",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class),
                            examples = @ExampleObject(name = "Listado de grupos", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "name": "Grupo A - Cálculo I",
                                            "capacity": 30,
                                            "classroomId": null,
                                            "teacherId": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
                                            "periodId": "a1b2c3d4-e5f6-4789-8abc-def012345678",
                                            "subjectId": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Active groups retrieved successfully",
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
    public ResponseEntity<ApiResponse<PaginatedResponse<GroupListDTO>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<GroupListDTO> groups = groupService.listGroups(page, size);
        return ResponseEntity.ok(ApiResponse.success(groups, "Active groups retrieved successfully"));
    }

    @Operation(summary = "List all groups including inactive", description = "Retrieves a paginated list of all groups. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all groups",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class),
                            examples = @ExampleObject(name = "Listado de todos los grupos", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "name": "Grupo A - Cálculo I",
                                            "capacity": 30,
                                            "classroomId": null,
                                            "teacherId": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
                                            "periodId": "a1b2c3d4-e5f6-4789-8abc-def012345678",
                                            "subjectId": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "All groups retrieved successfully",
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
    public ResponseEntity<ApiResponse<PaginatedResponse<GroupListDTO>>> listAllWithInactive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<GroupListDTO> groups = groupService.listAllGroupsIncludingInactive(page, size);
        return ResponseEntity.ok(ApiResponse.success(groups, "All groups retrieved successfully"));
    }

    @Operation(summary = "Get groups by status", description = "Retrieves a paginated list of groups filtered by status. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved groups by status",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class),
                            examples = @ExampleObject(name = "Grupos por estado", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "name": "Grupo A - Cálculo I",
                                            "capacity": 30,
                                            "classroomId": null,
                                            "teacherId": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
                                            "periodId": "a1b2c3d4-e5f6-4789-8abc-def012345678",
                                            "subjectId": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Groups retrieved successfully by status",
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
    public ResponseEntity<ApiResponse<PaginatedResponse<GroupListDTO>>> getByStatus(
            @PathVariable boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<GroupListDTO> groups = groupService.findByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(groups, "Groups retrieved successfully by status"));
    }

    @Operation(summary = "Search groups by name", description = "Searches active groups by name (case-insensitive partial match). Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved matching groups",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class),
                            examples = @ExampleObject(name = "Búsqueda de grupos", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "name": "Grupo A - Cálculo I",
                                            "capacity": 30,
                                            "classroomId": null,
                                            "teacherId": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
                                            "periodId": "a1b2c3d4-e5f6-4789-8abc-def012345678",
                                            "subjectId": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Groups retrieved successfully by name",
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
    public ResponseEntity<ApiResponse<PaginatedResponse<GroupListDTO>>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<GroupListDTO> groups = groupService.searchByName(name, page, size);
        return ResponseEntity.ok(ApiResponse.success(groups, "Groups retrieved successfully by name"));
    }

    @Operation(summary = "Get groups by classroom", description = "Retrieves active groups that have at least one active class hour in the given classroom. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved groups by classroom",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class),
                            examples = @ExampleObject(name = "Grupos por aula", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "name": "Grupo A - Cálculo I",
                                            "capacity": 30,
                                            "classroomId": null,
                                            "teacherId": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
                                            "periodId": "a1b2c3d4-e5f6-4789-8abc-def012345678",
                                            "subjectId": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Groups retrieved successfully by classroom",
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
    public ResponseEntity<ApiResponse<PaginatedResponse<GroupListDTO>>> getByClassroom(
            @PathVariable UUID classroomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<GroupListDTO> groups = groupService.findByClassroom(classroomId, page, size);
        return ResponseEntity.ok(ApiResponse.success(groups, "Groups retrieved successfully by classroom"));
    }

    @Operation(summary = "Get groups by subject", description = "Retrieves active groups for a specific subject. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved groups by subject",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class),
                            examples = @ExampleObject(name = "Grupos por asignatura", value = """
                                    {
                                      "Data": {
                                        "content": [
                                          {
                                            "id": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                            "name": "Grupo A - Cálculo I",
                                            "capacity": 30,
                                            "classroomId": null,
                                            "teacherId": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
                                            "periodId": "a1b2c3d4-e5f6-4789-8abc-def012345678",
                                            "subjectId": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
                                            "isActive": true
                                          }
                                        ],
                                        "pageNumber": 0,
                                        "pageSize": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "last": true
                                      },
                                      "Message": "Groups retrieved successfully by subject",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<GroupListDTO>>> getBySubject(
            @PathVariable UUID subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<GroupListDTO> groups = groupService.findBySubject(subjectId, page, size);
        return ResponseEntity.ok(ApiResponse.success(groups, "Groups retrieved successfully by subject"));
    }

    @Operation(summary = "Get group by ID", description = "Retrieves detailed information of a single group. Requires authentication.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the group",
                    content = @Content(schema = @Schema(implementation = GroupDetailDTO.class),
                            examples = @ExampleObject(name = "Detalle de grupo", value = """
                                    {
                                      "Data": {
                                        "id": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                        "name": "Grupo A - Cálculo I",
                                        "capacity": 30,
                                        "classroomId": null,
                                        "teacherId": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
                                        "periodId": "a1b2c3d4-e5f6-4789-8abc-def012345678",
                                        "subjectId": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
                                        "isActive": true
                                      },
                                      "Message": "Group retrieved successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(examples = @ExampleObject(name = "No encontrado", value = """
                            {
                              "Data": null,
                              "Message": "Group not found with ID: 8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                              "Errors": ["Group not found with ID: 8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GroupDetailDTO>> getGroupById(@PathVariable UUID id) {
        GroupDetailDTO group = groupService.findGroupById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));
        return ResponseEntity.ok(ApiResponse.success(group, "Group retrieved successfully"));
    }

    @Operation(summary = "Create a new group", description = "Creates a new group. Requires ADMINISTRATOR role. classroomId is optional; when provided, group capacity cannot exceed the classroom max capacity. Creating a group does not reserve the classroom globally.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Group created successfully",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class),
                            examples = @ExampleObject(name = "Grupo creado", value = """
                                    {
                                      "Data": {
                                        "id": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                        "name": "Grupo A - Cálculo I",
                                        "capacity": 30,
                                        "classroomId": null,
                                        "teacherId": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
                                        "periodId": "a1b2c3d4-e5f6-4789-8abc-def012345678",
                                        "subjectId": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
                                        "isActive": true
                                      },
                                      "Message": "Group created successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid group data",
                    content = @Content(examples = @ExampleObject(name = "Datos inválidos", value = """
                            {
                              "Data": null,
                              "Message": "Validation failed",
                              "Errors": ["name: must not be blank"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "A group with the same name, subject and period already exists",
                    content = @Content(examples = @ExampleObject(name = "Conflicto", value = """
                            {
                              "Data": null,
                              "Message": "A group with the same name, subject and period already exists",
                              "Errors": ["A group with the same name, subject and period already exists"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Capacity exceeds classroom max capacity",
                    content = @Content(examples = @ExampleObject(name = "Capacidad excedida", value = """
                            {
                              "Data": null,
                              "Message": "Group capacity exceeds classroom max capacity",
                              "Errors": ["Group capacity exceeds classroom max capacity"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        GroupResponse createdGroup = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdGroup, "Group created successfully"));
    }

    @Operation(summary = "Update an existing group", description = "Updates an existing group. Requires ADMINISTRATOR role. The group capacity cannot exceed the classroom max capacity.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Group updated successfully",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class),
                            examples = @ExampleObject(name = "Grupo actualizado", value = """
                                    {
                                      "Data": {
                                        "id": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                                        "name": "Grupo A - Cálculo I",
                                        "capacity": 32,
                                        "classroomId": null,
                                        "teacherId": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
                                        "periodId": "a1b2c3d4-e5f6-4789-8abc-def012345678",
                                        "subjectId": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
                                        "isActive": true
                                      },
                                      "Message": "Group updated successfully",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid group data or capacity exceeds classroom capacity",
                    content = @Content(examples = @ExampleObject(name = "Datos inválidos", value = """
                            {
                              "Data": null,
                              "Message": "Validation failed",
                              "Errors": ["capacity: must be greater than 0"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(examples = @ExampleObject(name = "No encontrado", value = """
                            {
                              "Data": null,
                              "Message": "Group not found with ID: 8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                              "Errors": ["Group not found with ID: 8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGroupRequest request) {
        GroupResponse updatedGroup = groupService.updateGroup(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedGroup, "Group updated successfully"));
    }

    @Operation(summary = "Delete a group", description = "Soft deletes a group by ID. Requires ADMINISTRATOR role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Group deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "Grupo eliminado", value = """
                                    {
                                      "Data": null,
                                      "Message": "Group deleted successfully (soft delete)",
                                      "Errors": []
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMINISTRATOR role",
                    content = @Content(examples = @ExampleObject(name = "Prohibido", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(examples = @ExampleObject(name = "No encontrado", value = """
                            {
                              "Data": null,
                              "Message": "Group not found with ID: 8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                              "Errors": ["Group not found with ID: 8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11"]
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Group deleted successfully (soft delete)"));
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
