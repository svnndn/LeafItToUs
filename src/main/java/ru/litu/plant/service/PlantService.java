package ru.litu.plant.service;

import ru.litu.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litu.plant.dto.PlantResponse;
import ru.litu.plant.mapper.PlantMapper;
import ru.litu.plant.repository.PlantRepository;

import java.util.List;

public interface PlantService {

    public List<PlantResponse> getAllPlants();

    public PlantResponse getPlantById(Long id);

    public PlantResponse getPlantByName(String name);

}
