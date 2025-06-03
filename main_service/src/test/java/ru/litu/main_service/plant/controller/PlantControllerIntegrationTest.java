package ru.litu.main_service.plant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.litu.main_service.plant.model.CareLevel;
import ru.litu.main_service.plant.model.Plant;
import ru.litu.main_service.plant.repository.PlantRepository;
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.model.Role;
import ru.litu.main_service.user.repository.RoleRepository;
import ru.litu.main_service.user.repository.UserRepository;
import ru.litu.main_service.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlantControllerIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(PlantControllerIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private Plant plant1;
    private Plant plant2;

    @BeforeEach
    void setUp() throws Exception {
        log.info("=== Подготовка: очистка базы и создание тестовых растений и пользователя ===");

        plantRepository.deleteAll();
        userRepository.deleteAll();

        if (roleRepository.getByName("ROLE_USER").isEmpty()) {
            Role roleUser = new Role();
            roleUser.setName("ROLE_USER");
            roleRepository.save(roleUser);
            log.info("Роль ROLE_USER создана");
        }

        if (userRepository.findByUsername("testuser").isEmpty()) {
            NewUserDto newUser = new NewUserDto("Test", "testuser", "test@mail.com", "password", "password");
            userService.add(newUser);
            log.info("Пользователь testuser создан");
        } else {
            log.info("Пользователь testuser уже существует");
        }

        plant1 = new Plant();
        plant1.setName("Ficus");
        plant1.setDescription("Тропическое растение");
        plant1.setImageUrl("http://example.com/ficus.jpg");
        plant1.setType("Домашнее");
        plant1.setCareLevel(CareLevel.MEDIUM);
        plant1.setWateringFrequency("1 раз в неделю");
        plant1.setLightingNeeds("Яркий свет");
        plant1.setFertilizingFrequency("Раз в месяц");

        plant2 = new Plant();
        plant2.setName("Aloe Vera");
        plant2.setDescription("Суккулентное растение");
        plant2.setImageUrl("http://example.com/aloe.jpg");
        plant2.setType("Суккулент");
        plant2.setCareLevel(CareLevel.LOW);
        plant2.setWateringFrequency("1 раз в 2 недели");
        plant2.setLightingNeeds("Средний свет");
        plant2.setFertilizingFrequency("Раз в 2 месяца");

        plantRepository.save(plant1);
        plantRepository.save(plant2);

        log.info("Тестовые растения созданы");
    }

    private String obtainJwtToken() throws Exception {
        log.info("Получение JWT токена для пользователя testuser");

        var result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andReturn();

        var responseJson = result.getResponse().getContentAsString();
        String token = "Bearer " + objectMapper.readTree(responseJson).get("token").asText();
        log.info("JWT токен получен: {}", token);
        return token;
    }

    @Test
    @DisplayName("GET /plant — должен вернуть список всех растений с JWT")
    void getAllPlants_ShouldReturnList() throws Exception {
        String token = obtainJwtToken();

        mockMvc.perform(get("/plant")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    @DisplayName("GET /plant/name/{name} — должен вернуть растение по имени с JWT")
    void getPlantByName_ShouldReturnPlant() throws Exception {
        String token = obtainJwtToken();

        mockMvc.perform(get("/plant/name/{name}", "Ficus")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ficus"))
                .andExpect(jsonPath("$.description").value("Тропическое растение"));
    }

    @Test
    @DisplayName("GET /plant/id/{id} — должен вернуть растение по ID с JWT")
    void getPlantById_ShouldReturnPlant() throws Exception {
        String token = obtainJwtToken();

        Long id = plant1.getId();

        mockMvc.perform(get("/plant/id/{id}", id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ficus"))
                .andExpect(jsonPath("$.description").value("Тропическое растение"));
    }

    @Test
    @DisplayName("GET /plant/name/{name} — должен вернуть 404, если растение не найдено с JWT")
    void getPlantByName_NotFound() throws Exception {
        String token = obtainJwtToken();

        mockMvc.perform(get("/plant/name/{name}", "UnknownPlant")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
