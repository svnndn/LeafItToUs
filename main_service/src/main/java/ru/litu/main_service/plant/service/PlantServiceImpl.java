package ru.litu.main_service.plant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.litu.main_service.exception.ObjectNotFoundException;
import ru.litu.main_service.plant.dto.PlantResponse;
import ru.litu.main_service.plant.mapper.PlantMapper;
import ru.litu.main_service.plant.repository.PlantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlantServiceImpl implements PlantService {
    private final PlantRepository plantRepository;
    private final PlantMapper plantMapper;

    @Override
    public List<PlantResponse> getAllPlants() {
        return plantMapper.toResponseList(plantRepository.findAll());
    }

    @Override
    public PlantResponse getPlantById(Long id) {
        return plantRepository.findById(id)
                .map(plantMapper::toResponse)
                .orElseThrow(() -> new ObjectNotFoundException("plant with id %d not found".formatted(id)));
    }

    @Override
    public PlantResponse getPlantByName(String name) {
        return plantRepository.findByName(name)
                .map(plantMapper::toResponse)
                .orElseThrow(() -> new ObjectNotFoundException("plant with name %s not found".formatted(name)));
    }
}
