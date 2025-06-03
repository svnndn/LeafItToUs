package ru.litu.notification_service.telegram.service;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.litu.notification_service.telegram.config.TelegramBotProperties;

import static org.junit.jupiter.api.Assertions.*;

class TelegramServiceTest {

    private static final Logger log = LoggerFactory.getLogger(TelegramServiceTest.class);

    private TelegramService telegramService;

    @BeforeEach
    void setUp() {
        TelegramBotProperties properties = new TelegramBotProperties();
        properties.setApiUrl("https://api.telegram.org");
        properties.setToken("dummy-token");
        properties.setUsername("test_bot");

        telegramService = new TelegramService(properties);
        log.info("TelegramService инициализирован с properties: {}", properties);
    }

    @Test
    @DisplayName("generateRegistrationLink должен возвращать корректную ссылку")
    void generateRegistrationLink_ShouldReturnCorrectLink() {
        Long userId = 123L;
        String expectedLink = "https://t.me/test_bot?start=123";

        log.info("Тестируем generateRegistrationLink для userId={}", userId);
        String actualLink = telegramService.generateRegistrationLink(userId);
        log.info("Полученная ссылка: {}", actualLink);

        assertEquals(expectedLink, actualLink, "Сформированная ссылка не совпадает с ожидаемой");
    }
}
