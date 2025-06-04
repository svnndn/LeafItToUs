package ru.litu.notification_service.telegram.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.litu.notification_service.TestLoggerExtension;
import ru.litu.notification_service.telegram.model.TelegramUser;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(TestLoggerExtension.class)
class TelegramUserRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(TelegramUserRepositoryTest.class);

    @Autowired
    private TelegramUserRepository telegramUserRepository;

    private TelegramUser createTelegramUser(Long userId, String chatId) {
        TelegramUser user = new TelegramUser();
        user.setUserId(userId);
        user.setChatId(chatId);
        return telegramUserRepository.save(user);
    }

    @Test
    @DisplayName("findByUserId: должен найти пользователя по userId")
    void findByUserId_ShouldFindUser() {
        log.info("Запуск теста findByUserId_ShouldFindUser");

        TelegramUser user = createTelegramUser(123L, "chat_abc");
        log.info("Создан TelegramUser с userId={} и chatId={}", user.getUserId(), user.getChatId());

        Optional<TelegramUser> found = telegramUserRepository.findByUserId(123L);

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(123L);
        assertThat(found.get().getChatId()).isEqualTo("chat_abc");
        log.info("Пользователь найден: userId={}, chatId={}", found.get().getUserId(), found.get().getChatId());
    }

    @Test
    @DisplayName("findByUserId: должен вернуть пустой Optional, если пользователь не найден")
    void findByUserId_ShouldReturnEmpty() {
        log.info("Запуск теста findByUserId_ShouldReturnEmpty");

        Optional<TelegramUser> found = telegramUserRepository.findByUserId(999L);

        assertThat(found).isEmpty();
        log.info("Пользователь с userId=999 не найден");
    }

    @Test
    @DisplayName("findByChatId: должен найти пользователя по chatId")
    void findByChatId_ShouldFindUser() {
        log.info("Запуск теста findByChatId_ShouldFindUser");

        TelegramUser user = createTelegramUser(456L, "chat_xyz");
        log.info("Создан TelegramUser с userId={} и chatId={}", user.getUserId(), user.getChatId());

        Optional<TelegramUser> found = telegramUserRepository.findByChatId("chat_xyz");

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(456L);
        assertThat(found.get().getChatId()).isEqualTo("chat_xyz");
        log.info("Пользователь найден: userId={}, chatId={}", found.get().getUserId(), found.get().getChatId());
    }

    @Test
    @DisplayName("findByChatId: должен вернуть пустой Optional, если пользователь не найден")
    void findByChatId_ShouldReturnEmpty() {
        log.info("Запуск теста findByChatId_ShouldReturnEmpty");

        Optional<TelegramUser> found = telegramUserRepository.findByChatId("nonexistent_chat");

        assertThat(found).isEmpty();
        log.info("Пользователь с chatId='nonexistent_chat' не найден");
    }
}
