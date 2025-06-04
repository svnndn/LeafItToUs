package ru.litu.main_service.plant.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.litu.main_service.TestLoggerExtension;
import ru.litu.main_service.plant.model.CareLevel;
import ru.litu.main_service.plant.model.Plant;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(TestLoggerExtension.class)
class PlantRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(PlantRepositoryTest.class);

    @Autowired
    private PlantRepository plantRepository;

    private Plant createPlant(String name) {
        Plant plant = new Plant();
        plant.setName(name);
        plant.setDescription("Описание " + name);
        plant.setImageUrl("http://example.com/" + name + ".jpg");
        plant.setType("Type-" + name);
        plant.setCareLevel(CareLevel.MEDIUM);
        plant.setWateringFrequency("1 раз в неделю");
        plant.setLightingNeeds("Яркий рассеянный свет");
        plant.setFertilizingFrequency("2 раза в месяц");
        return plant;
    }

    @Test
    @DisplayName("findByName: должен вернуть растение по имени")
    void findByName_ShouldReturnPlant() {
        log.info("Запуск теста findByName_ShouldReturnPlant");

        Plant plant = createPlant("Ficus");
        log.info("Сохранение растения: {}", plant.getName());
        plantRepository.save(plant);

        log.info("Поиск растения по имени: {}", plant.getName());
        Optional<Plant> result = plantRepository.findByName("Ficus");
        log.info("Найдено растение: {}", result);

        assertTrue(result.isPresent());
        assertEquals("Ficus", result.get().getName());

        log.info("Растение успешно найдено по имени");
    }

    @Test
    @DisplayName("findByName: должен вернуть пустой Optional, если растение не найдено")
    void findByName_ShouldReturnEmpty() {
        log.info("Запуск теста findByName_ShouldReturnEmpty");

        Optional<Plant> result = plantRepository.findByName("NonexistentPlant");
        log.info("Результат поиска: {}", result);

        assertTrue(result.isEmpty());
        log.info("Тест успешно прошёл — растение не найдено");
    }

    @Test
    @DisplayName("saveAll и findAll: должен сохранить и вернуть список растений")
    void saveAndFindAllPlants_ShouldReturnPlants() {
        log.info("Запуск теста saveAndFindAllPlants_ShouldReturnPlants");

        Plant p1 = createPlant("Cactus");
        Plant p2 = createPlant("Monstera");

        plantRepository.saveAll(List.of(p1, p2));
        log.info("Сохранены растения: {}, {}", p1.getName(), p2.getName());

        List<Plant> allPlants = plantRepository.findAll();
        log.info("Найдено растений: {}", allPlants.size());

        assertThat(allPlants).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allPlants).extracting(Plant::getName)
                .contains("Cactus", "Monstera");

        log.info("Все растения успешно найдены");
    }
}
