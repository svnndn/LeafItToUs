package ru.litu.notification_service.telegram.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TelegramRegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ru.litu.notification_service.telegram.service.TelegramService telegramService;

    @Test
    @DisplayName("GET /api/telegram/registration-link/{userId} — редирект на URL регистрации Telegram")
    void redirectToTelegram_ShouldReturnRedirect() throws Exception {
        Long userId = 123L;
        String expectedUrl = "https://t.me/registration_link_for_user_123";

        Mockito.when(telegramService.generateRegistrationLink(userId)).thenReturn(expectedUrl);

        mockMvc.perform(get("/api/telegram/registration-link/{userId}", userId))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, expectedUrl));
    }
}
