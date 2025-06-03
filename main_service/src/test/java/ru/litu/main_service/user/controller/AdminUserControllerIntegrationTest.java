package ru.litu.main_service.user.controller;

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
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.model.Role;
import ru.litu.main_service.user.model.User;
import ru.litu.main_service.user.repository.RoleRepository;
import ru.litu.main_service.user.repository.UserRepository;
import ru.litu.main_service.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminUserControllerIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(AdminUserControllerIntegrationTest.class);

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
        log.info("=== Подготовка: создание администратора ===");

        if (roleRepository.getByName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
            log.info("Роль ROLE_ADMIN создана");
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            NewUserDto admin = new NewUserDto("Admin", "admin", "admin@mail.com", "adminpass", "adminpass");
            User adminUser = userService.add(admin);
            userRepository.save(adminUser);
            log.info("Админ admin создан");
        }
    }

    private String obtainJwtToken() throws Exception {
        log.info("Получение JWT токена для администратора");

        var result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "admin")
                        .param("password", "adminpass"))
                .andExpect(status().isOk())
                .andReturn();

        var responseJson = result.getResponse().getContentAsString();
        String token = "Bearer " + objectMapper.readTree(responseJson).get("token").asText();
        log.info("JWT токен администратора получен: {}", token);
        return token;
    }

    @Test
    @DisplayName("GET /admin/users — получить список всех пользователей с JWT администратора")
    void getAllUsersWithAdminJwt() throws Exception {
        String token = obtainJwtToken();

        mockMvc.perform(get("/admin/users")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNumber());
    }

    @Test
    @DisplayName("DELETE /admin/users/{id} — удалить пользователя по id с правами администратора")
    void deleteUserByAdmin() throws Exception {
        log.info("Создание пользователя для удаления админом");
        NewUserDto userToDelete = new NewUserDto("To Delete", "todelete", "del@mail.com", "password", "password");
        User createdUser = userService.add(userToDelete);

        String token = obtainJwtToken();

        mockMvc.perform(delete("/admin/users/" + createdUser.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }
}
