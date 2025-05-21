package ru.litu.plant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litu.plant.model.Plant;

import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    Optional<Plant> findByName(String name);
}
