package ru.litu.main_service.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.model.Role;
import ru.litu.main_service.user.model.User;
import ru.litu.main_service.user.repository.RoleRepository;
import ru.litu.main_service.user.repository.UserRepository;
import ru.litu.main_service.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(UserControllerIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        log.info("=== Подготовка: создание тестового пользователя ===");

        if (roleRepository.getByName("ROLE_USER").isEmpty()) {
            Role roleUser = new Role();
            roleUser.setName("ROLE_USER");
            roleRepository.save(roleUser);
            log.info("Роль ROLE_USER создана");
        } else {
            log.info("Роль ROLE_USER уже существует");
        }

        if (userRepository.findByUsername("testuser").isEmpty()) {
            NewUserDto newUser = new NewUserDto("Test", "testuser", "test@mail.com", "password", "password");
            User result = userService.add(newUser);
            log.info("Пользователь успешно создан: {}", result.getUsername());
        } else {
            log.info("Пользователь testuser уже существует");
        }
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
    @DisplayName("GET /users/{id} с JWT")
    void getUserWithRealJwt() throws Exception {
        log.info("Запуск теста: получение пользователя с JWT");
        String token = obtainJwtToken();
        NewUserDto newUser1 = new NewUserDto();
        if (userRepository.findByUsername("testuser1").isEmpty()) {
            newUser1 = new NewUserDto("Testuser1", "testuser1", "test1@mail.com", "password", "password");
        }
        User result = userService.add(newUser1);
        Long userId = result.getId();

        mockMvc.perform(get("/users/" + userId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Testuser1"))
                .andExpect(jsonPath("$.email").value("test1@mail.com"));

        log.info("Тест успешно пройден: получение пользователя с JWT");
    }


    @Test
    @DisplayName("POST /users с JWT — создание нового пользователя")
    void createUserWithJwt() throws Exception {
        log.info("Запуск теста: создание нового пользователя с JWT");
        String token = obtainJwtToken();

        NewUserDto newUserDto = new NewUserDto("New User", "newuser", "newuser@mail.com", "password", "password");

        mockMvc.perform(post("/users")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@mail.com"))
                .andExpect(jsonPath("$.name").value("New User"));

        log.info("Тест успешно пройден: создание нового пользователя с JWT");
    }

    @Test
    @DisplayName("PATCH /users/{id} с JWT — обновление пользователя")
    void updateUserWithJwt() throws Exception {
        log.info("Запуск теста: обновление пользователя с JWT");
        String token = obtainJwtToken();

        User user = userRepository.findByUsername("testuser").orElseThrow();
        Long userId = user.getId();

        String updateJson = """
    {
      "name": "Updated User",
      "email": "update@email.com"
    }
    """;

        mockMvc.perform(patch("/users/" + userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("update@email.com"));

        log.info("Тест успешно пройден: обновление пользователя с JWT");
    }


    @Test
    @DisplayName("DELETE /users/{id} с JWT — удаление пользователя")
    void deleteUserWithJwt() throws Exception {
        log.info("Запуск теста: удаление пользователя с JWT");

        log.info("Создание пользователя для дальнейшего удаления");
        NewUserDto newUserDto = new NewUserDto("Delete Me", "deleteme", "deleteme@mail.com", "password", "password");
        User created = userService.add(newUserDto);

        String token = obtainJwtToken();

        log.info("Удаление пользователя {}", created.getUsername());
        mockMvc.perform(delete("/users/" + created.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        log.info("Тест успешно пройден: удаление пользователя с JWT");
    }

}
