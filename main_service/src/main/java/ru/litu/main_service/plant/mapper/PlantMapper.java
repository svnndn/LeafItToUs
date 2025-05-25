package ru.litu.main_service.plant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.litu.main_service.plant.dto.PlantResponse;
import ru.litu.main_service.plant.model.Plant;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlantMapper {
    PlantResponse toResponse(Plant plant);

    List<PlantResponse> toResponseList(List<Plant> plants);
}
