package ru.litu.plant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.litu.plant.dto.PlantResponse;
import ru.litu.plant.service.PlantService;

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

    @GetMapping("/{name}")
    public PlantResponse findByName(String name){
        return plantService.getPlantByName(name);
    }

    @GetMapping("/{id}")
    public PlantResponse findById(Long id){
        return plantService.getPlantById(id);
    }

}
