package ru.litu.main_service.plant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.litu.main_service.TestLoggerExtension;
import ru.litu.main_service.exception.ObjectNotFoundException;
import ru.litu.main_service.plant.dto.PlantResponse;
import ru.litu.main_service.plant.mapper.PlantMapper;
import ru.litu.main_service.plant.model.CareLevel;
import ru.litu.main_service.plant.model.Plant;
import ru.litu.main_service.plant.repository.PlantRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(TestLoggerExtension.class)
class PlantServiceTest {

    private static final Logger log = LoggerFactory.getLogger(PlantServiceTest.class);

    @Mock private PlantRepository plantRepository;
    @Mock private PlantMapper plantMapper;

    @InjectMocks private PlantServiceImpl plantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        log.info("Моки и сервис PlantService инициализированы");
    }

    private Plant createTestPlant(Long id, String name) {
        Plant plant = new Plant();
        plant.setId(id);
        plant.setName(name);
        plant.setDescription("Test description");
        plant.setImageUrl("http://image.url");
        plant.setType("Succulent");
        plant.setCareLevel(CareLevel.LOW);
        plant.setWateringFrequency("Once a week");
        plant.setLightingNeeds("Indirect light");
        plant.setFertilizingFrequency("Once a month");
        return plant;
    }

    private PlantResponse createTestPlantResponse(Long id, String name) {
        return new PlantResponse(id, name, "Test description", "http://image.url", "Succulent",
                CareLevel.LOW, "Once a week", "Indirect light", "Once a month");
    }

    @Test
    @DisplayName("getAllPlants_ShouldReturnAllPlants")
    void getAllPlants_ShouldReturnAllPlants() {
        log.info("Запуск теста getAllPlants_ShouldReturnAllPlants");

        List<Plant> plants = List.of(createTestPlant(1L, "Aloe"), createTestPlant(2L, "Cactus"));
        List<PlantResponse> responses = List.of(
                createTestPlantResponse(1L, "Aloe"),
                createTestPlantResponse(2L, "Cactus")
        );

        when(plantRepository.findAll()).thenReturn(plants);
        when(plantMapper.toResponseList(plants)).thenReturn(responses);

        List<PlantResponse> result = plantService.getAllPlants();
        log.info("Полученные растения: {}", result);

        assertEquals(2, result.size());
        assertEquals("Aloe", result.get(0).getName());
        verify(plantRepository).findAll();

        log.info("Тест getAllPlants успешно завершён");
    }

    @Test
    @DisplayName("getPlantById_ShouldReturnPlant")
    void getPlantById_ShouldReturnPlant() {
        log.info("Запуск теста getPlantById_ShouldReturnPlant");

        Plant plant = createTestPlant(1L, "Monstera");
        PlantResponse response = createTestPlantResponse(1L, "Monstera");

        when(plantRepository.findById(1L)).thenReturn(Optional.of(plant));
        when(plantMapper.toResponse(plant)).thenReturn(response);

        PlantResponse result = plantService.getPlantById(1L);
        log.info("Получено растение по ID: {}", result);

        assertEquals("Monstera", result.getName());
        assertEquals("Succulent", result.getType());
        verify(plantRepository).findById(1L);
    }

    @Test
    @DisplayName("getPlantById_ShouldThrow_WhenNotFound")
    void getPlantById_ShouldThrow_WhenNotFound() {
        log.info("Запуск теста getPlantById_ShouldThrow_WhenNotFound");

        when(plantRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ObjectNotFoundException.class,
                () -> plantService.getPlantById(99L));

        assertTrue(ex.getMessage().contains("plant with id"));
        log.warn("Ожидаемое исключение: {}", ex.getMessage());
    }

    @Test
    @DisplayName("getPlantByName_ShouldReturnPlant")
    void getPlantByName_ShouldReturnPlant() {
        log.info("Запуск теста getPlantByName_ShouldReturnPlant");

        Plant plant = createTestPlant(2L, "Fern");
        PlantResponse response = createTestPlantResponse(2L, "Fern");

        when(plantRepository.findByName("Fern")).thenReturn(Optional.of(plant));
        when(plantMapper.toResponse(plant)).thenReturn(response);

        PlantResponse result = plantService.getPlantByName("Fern");
        log.info("Получено растение по имени: {}", result);

        assertEquals("Fern", result.getName());
        verify(plantRepository).findByName("Fern");
    }

    @Test
    @DisplayName("getPlantByName_ShouldThrow_WhenNotFound")
    void getPlantByName_ShouldThrow_WhenNotFound() {
        log.info("Запуск теста getPlantByName_ShouldThrow_WhenNotFound");

        when(plantRepository.findByName("Unknown")).thenReturn(Optional.empty());

        Exception ex = assertThrows(ObjectNotFoundException.class,
                () -> plantService.getPlantByName("Unknown"));

        assertTrue(ex.getMessage().contains("plant with name"));
        log.warn("Ожидаемое исключение: {}", ex.getMessage());
    }
}
