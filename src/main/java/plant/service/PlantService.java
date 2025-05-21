package plant.service;

import exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plant.dto.PlantResponse;
import plant.mapper.PlantMapper;
import plant.repository.PlantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantService {

    private final PlantRepository plantRepository;
    private final PlantMapper plantMapper;

    public List<PlantResponse> getAllPlants() {
        return plantMapper.toResponseList(plantRepository.findAll());
    }

    public PlantResponse getPlantById(Long id) {
        return plantRepository.findById(id)
                .map(plantMapper::toResponse)
                .orElseThrow(() -> new ObjectNotFoundException("plant with id %d not found".formatted(id)));
    }

    public PlantResponse getPlantByName(String name) {
        return plantRepository.findByName(name)
                .map(plantMapper::toResponse)
                .orElseThrow(() -> new ObjectNotFoundException("plant with name %s not found".formatted(name)));
    }


}
