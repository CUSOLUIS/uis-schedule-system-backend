package com.uis.schedule.backend.service.implementation;

import com.uis.schedule.backend.persistence.entity.BuildingEntity;
import com.uis.schedule.backend.persistence.repository.BuildingRepository;
import com.uis.schedule.backend.presentation.dto.BuildingDTO;
import com.uis.schedule.backend.presentation.dto.CreateBuildingRequest;
import com.uis.schedule.backend.presentation.dto.PaginatedResponse;
import com.uis.schedule.backend.presentation.dto.UpdateBuildingRequest;
import com.uis.schedule.backend.service.interfaces.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BuildingDTO> listBuildings(int page, int size, boolean onlyActive) {
        Page<BuildingEntity> buildings = onlyActive
                ? buildingRepository.findAllByIsActiveTrue(PageRequest.of(page, size))
                : buildingRepository.findAll(PageRequest.of(page, size));
        return PaginatedResponse.<BuildingDTO>builder()
                .content(buildings.getContent().stream().map(this::toDto).toList())
                .pageNumber(buildings.getNumber())
                .pageSize(buildings.getSize())
                .totalElements(buildings.getTotalElements())
                .totalPages(buildings.getTotalPages())
                .last(buildings.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingDTO findById(UUID id) {
        return toDto(buildingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Building not found with ID: " + id)));
    }

    @Override
    public BuildingDTO create(CreateBuildingRequest request) {
        if (buildingRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new IllegalArgumentException("Building code already exists: " + request.getCode());
        }
        BuildingEntity saved = buildingRepository.save(BuildingEntity.builder()
                .code(request.getCode().trim().toUpperCase())
                .name(request.getName().trim())
                .faculty(request.getFaculty())
                .preferenceOrder(request.getPreferenceOrder())
                .hasAc(Boolean.TRUE.equals(request.getHasAc()))
                .hasComputerLab(Boolean.TRUE.equals(request.getHasComputerLab()))
                .campus(request.getCampus())
                .isActive(true)
                .build());
        return toDto(saved);
    }

    @Override
    public BuildingDTO update(UUID id, UpdateBuildingRequest request) {
        BuildingEntity building = buildingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Building not found with ID: " + id));

        if (request.getCode() != null && !request.getCode().isBlank()) {
            String code = request.getCode().trim().toUpperCase();
            buildingRepository.findByCodeIgnoreCase(code)
                    .filter(existing -> !existing.getBuildingId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Building code already exists: " + code);
                    });
            building.setCode(code);
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            building.setName(request.getName().trim());
        }
        if (request.getFaculty() != null) {
            building.setFaculty(request.getFaculty());
        }
        if (request.getPreferenceOrder() != null) {
            building.setPreferenceOrder(request.getPreferenceOrder());
        }
        if (request.getHasAc() != null) {
            building.setHasAc(request.getHasAc());
        }
        if (request.getHasComputerLab() != null) {
            building.setHasComputerLab(request.getHasComputerLab());
        }
        if (request.getCampus() != null) {
            building.setCampus(request.getCampus());
        }
        if (request.getActive() != null) {
            building.setActive(request.getActive());
        }
        return toDto(buildingRepository.save(building));
    }

    private BuildingDTO toDto(BuildingEntity entity) {
        return BuildingDTO.builder()
                .id(entity.getBuildingId())
                .code(entity.getCode())
                .name(entity.getName())
                .faculty(entity.getFaculty())
                .preferenceOrder(entity.getPreferenceOrder())
                .hasAc(entity.isHasAc())
                .hasComputerLab(entity.isHasComputerLab())
                .campus(entity.getCampus())
                .isActive(entity.isActive())
                .build();
    }
}
