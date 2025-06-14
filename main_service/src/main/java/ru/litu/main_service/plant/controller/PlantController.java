package ru.litu.main_service.plant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.litu.main_service.plant.dto.PlantResponse;
import ru.litu.main_service.plant.service.PlantService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plant")
public class PlantController {

    private final PlantService plantService;

    @GetMapping
    public List<PlantResponse> findAll(){
        return plantService.getAllPlants();
    }

    @GetMapping("/name/{name}")
    public PlantResponse findByName(@PathVariable String name) {
        return plantService.getPlantByName(name);
    }

    @GetMapping("/id/{id}")
    public PlantResponse findById(@PathVariable Long id) {
        return plantService.getPlantById(id);
    }
}
