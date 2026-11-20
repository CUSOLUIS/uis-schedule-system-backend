package com.uis.schedule.backend.presentation.controller;

import com.uis.schedule.backend.presentation.dto.*;
import com.uis.schedule.backend.service.interfaces.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/buildings")
@Tag(name = "Buildings", description = "Building catalog endpoints")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @Operation(summary = "List buildings")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<BuildingDTO>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "true") boolean onlyActive) {
        return ResponseEntity.ok(ApiResponse.success(
                buildingService.listBuildings(page, size, onlyActive),
                "Buildings retrieved successfully"));
    }

    @Operation(summary = "Get building by id")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BuildingDTO>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(buildingService.findById(id), "Building retrieved successfully"));
    }

    @Operation(summary = "Create building")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<BuildingDTO>> create(@Valid @RequestBody CreateBuildingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(buildingService.create(request), "Building created successfully"));
    }

    @Operation(summary = "Update building")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<BuildingDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBuildingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(buildingService.update(id, request), "Building updated successfully"));
    }
}
