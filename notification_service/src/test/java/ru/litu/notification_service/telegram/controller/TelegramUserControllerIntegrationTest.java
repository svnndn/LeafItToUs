package ru.litu.notification_service.telegram.controller;

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
import ru.litu.notification_service.telegram.model.TelegramUser;
import ru.litu.notification_service.telegram.repository.TelegramUserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TelegramUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TelegramUserRepository telegramUserRepository;

    @BeforeEach
    void setUp() {
        telegramUserRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/telegram/register — успешная регистрация нового пользователя")
    void register_ShouldRegisterNewUser() throws Exception {
        mockMvc.perform(post("/api/telegram/register")
                        .param("userId", "100")
                        .param("chatId", "chat_100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь зарегистрирован."));

        assertThat(telegramUserRepository.findByUserId(100L)).isPresent()
                .get().extracting(TelegramUser::getChatId).isEqualTo("chat_100");
    }

    @Test
    @DisplayName("POST /api/telegram/register — регистрация с существующим userId должна вернуть ошибку")
    void register_ShouldReturnBadRequestIfUserIdExists() throws Exception {
        TelegramUser existingUser = new TelegramUser();
        existingUser.setUserId(200L);
        existingUser.setChatId("chat_200");
        telegramUserRepository.save(existingUser);

        mockMvc.perform(post("/api/telegram/register")
                        .param("userId", "200")
                        .param("chatId", "chat_new"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Пользователь с таким userId уже зарегистрирован."));
    }

    @Test
    @DisplayName("GET /api/telegram/getChatId — получить chatId по userId")
    void getChatId_ShouldReturnChatId() throws Exception {
        TelegramUser user = new TelegramUser();
        user.setUserId(300L);
        user.setChatId("chat_300");
        telegramUserRepository.save(user);

        mockMvc.perform(get("/api/telegram/getChatId")
                        .param("userId", "300"))
                .andExpect(status().isOk())
                .andExpect(content().string("chat_300"));
    }

    @Test
    @DisplayName("GET /api/telegram/getChatId — если userId не найден, вернуть 404")
    void getChatId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/telegram/getChatId")
                        .param("userId", "99999"))
                .andExpect(status().isNotFound());
    }
}
