package com.uis.schedule.backend.service.interfaces;

import com.uis.schedule.backend.presentation.dto.BuildingDTO;
import com.uis.schedule.backend.presentation.dto.CreateBuildingRequest;
import com.uis.schedule.backend.presentation.dto.PaginatedResponse;
import com.uis.schedule.backend.presentation.dto.UpdateBuildingRequest;

import java.util.UUID;

public interface BuildingService {

    PaginatedResponse<BuildingDTO> listBuildings(int page, int size, boolean onlyActive);

    BuildingDTO findById(UUID id);

    BuildingDTO create(CreateBuildingRequest request);

    BuildingDTO update(UUID id, UpdateBuildingRequest request);
}
