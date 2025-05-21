package ru.litu.plant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.litu.plant.dto.PlantResponse;
import ru.litu.plant.model.Plant;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlantMapper {

    PlantResponse toResponse(Plant plant);

    List<PlantResponse> toResponseList(List<Plant> plants);
}
