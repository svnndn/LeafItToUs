package ru.litu.plant.service;

import ru.litu.plant.dto.PlantResponse;
import java.util.List;

public interface PlantService {
    List<PlantResponse> getAllPlants();
    PlantResponse getPlantById(Long id);
    PlantResponse getPlantByName(String name);
}
