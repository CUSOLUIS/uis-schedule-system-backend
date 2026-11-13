package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.ApiResponse;
import com.uis.schedule.backend.presentation.dto.TeacherScheduleDTO;
import com.uis.schedule.backend.service.interfaces.TeacherScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller for teacher schedule queries.
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/teachers")
@Tag(name = "Teachers", description = "Teacher schedule endpoints")
public class TeacherController {

    static final String SCHEDULE_RETRIEVED = "Teacher schedule retrieved successfully";
    static final String SCHEDULE_EMPTY = "The teacher has no assigned classes";

    private final TeacherScheduleService teacherScheduleService;

    public TeacherController(TeacherScheduleService teacherScheduleService) {
        this.teacherScheduleService = teacherScheduleService;
    }

    @Operation(summary = "Get the authenticated teacher's weekly schedule",
            description = "Returns the active class hours of the groups assigned to the authenticated teacher, "
                    + "one entry per day, sorted by day and start time. Requires TEACHER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Schedule retrieved (entries is empty when the teacher has no assigned classes)",
                    content = @Content(schema = @Schema(implementation = TeacherScheduleDTO.class),
                            examples = {
                                    @ExampleObject(name = "Horario del profesor", value = SCHEDULE_EXAMPLE),
                                    @ExampleObject(name = "Profesor sin asignaciones", value = EMPTY_EXAMPLE)
                            })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires TEACHER role",
                    content = @Content(examples = @ExampleObject(name = "Acceso denegado", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Authenticated user is not linked to a teacher profile",
                    content = @Content(examples = @ExampleObject(name = "Sin perfil docente", value = ERROR_404_ME))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @GetMapping("/me/schedule")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherScheduleDTO>> getMySchedule(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        TeacherScheduleDTO schedule = teacherScheduleService.getScheduleForCurrentTeacher(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(schedule, messageFor(schedule)));
    }

    @Operation(summary = "Get a teacher's weekly schedule by teacher ID",
            description = "Returns the active class hours of the groups assigned to the teacher, one entry per day, "
                    + "sorted by day and start time. ADMINISTRATOR can query any teacher; TEACHER only their own schedule.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Schedule retrieved (entries is empty when the teacher has no assigned classes)",
                    content = @Content(schema = @Schema(implementation = TeacherScheduleDTO.class),
                            examples = {
                                    @ExampleObject(name = "Horario del profesor", value = SCHEDULE_EXAMPLE),
                                    @ExampleObject(name = "Profesor sin asignaciones", value = EMPTY_EXAMPLE)
                            })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid teacher ID format",
                    content = @Content(examples = @ExampleObject(name = "ID inválido", value = ERROR_400))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Token missing or invalid",
                    content = @Content(examples = @ExampleObject(name = "No autorizado", value = ERROR_401))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - A teacher requested another teacher's schedule",
                    content = @Content(examples = @ExampleObject(name = "Acceso denegado", value = ERROR_403))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Teacher not found",
                    content = @Content(examples = @ExampleObject(name = "Profesor no encontrado", value = ERROR_404))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(examples = @ExampleObject(name = "Error interno", value = ERROR_500)))
    })
    @GetMapping("/{teacherId}/schedule")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TEACHER')")
    public ResponseEntity<ApiResponse<TeacherScheduleDTO>> getScheduleByTeacher(
            @PathVariable UUID teacherId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        TeacherScheduleDTO schedule = teacherScheduleService.getScheduleByTeacherId(teacherId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(schedule, messageFor(schedule)));
    }

    // El mensaje distingue "sin asignaciones" para que el cliente no tenga que inferirlo.
    private static String messageFor(TeacherScheduleDTO schedule) {
        return schedule.getEntries() == null || schedule.getEntries().isEmpty()
                ? SCHEDULE_EMPTY
                : SCHEDULE_RETRIEVED;
    }

    private static final String SCHEDULE_EXAMPLE = """
            {
              "Data": {
                "teacherId": "10000001-0000-0000-0000-000000000001",
                "teacherName": "Ana Pérez",
                "department": "Ingeniería de Sistemas",
                "entries": [
                  {
                    "classHourId": "1a2b3c4d-5e6f-4789-8abc-def012345679",
                    "dayName": "lunes",
                    "startTime": "08:00:00",
                    "endTime": "10:00:00",
                    "groupId": "8f14e45f-ceea-467e-9d6c-8e2b8a3f9c11",
                    "groupName": "A1",
                    "groupCapacity": 35,
                    "subjectCode": "22966",
                    "subjectName": "Bases de Datos",
                    "subjectCredits": 4,
                    "classroomNumber": "301",
                    "building": "Edificio de Ingeniería de Sistemas"
                  }
                ]
              },
              "Message": "Teacher schedule retrieved successfully",
              "Errors": []
            }
            """;

    private static final String EMPTY_EXAMPLE = """
            {
              "Data": {
                "teacherId": "10000001-0000-0000-0000-000000000001",
                "teacherName": "Ana Pérez",
                "department": "Ingeniería de Sistemas",
                "entries": []
              },
              "Message": "The teacher has no assigned classes",
              "Errors": []
            }
            """;

    private static final String ERROR_400 = """
            {
              "Data": null,
              "Message": "Invalid value for parameter 'teacherId'",
              "Errors": ["Invalid value for parameter 'teacherId'"]
            }
            """;

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

    private static final String ERROR_404 = """
            {
              "Data": null,
              "Message": "Teacher not found with ID: 10000001-0000-0000-0000-000000000099",
              "Errors": ["Teacher not found with ID: 10000001-0000-0000-0000-000000000099"]
            }
            """;

    private static final String ERROR_404_ME = """
            {
              "Data": null,
              "Message": "The authenticated user is not linked to a teacher profile.",
              "Errors": ["The authenticated user is not linked to a teacher profile."]
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
