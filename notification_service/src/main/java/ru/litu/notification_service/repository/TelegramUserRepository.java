package ru.litu.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litu.notification_service.model.TelegramUser;

import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findByUserId(Long userId);
    Optional<TelegramUser> findByChatId(String chatId);
}
