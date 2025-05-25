package ru.litu.main_service.plant.service;

import ru.litu.main_service.plant.dto.PlantResponse;

import java.util.List;

public interface PlantService {
    List<PlantResponse> getAllPlants();
    PlantResponse getPlantById(Long id);
    PlantResponse getPlantByName(String name);
}
