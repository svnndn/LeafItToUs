package ru.litu.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.litu.notification_service.config.TelegramBotProperties;
import ru.litu.notification_service.repository.TelegramUserRepository;
import ru.litu.notification_service.telegramLongPolling.MyTelegramBot;

@SpringBootApplication
@EntityScan("ru.litu.notification_service.model")
@EnableConfigurationProperties(TelegramBotProperties.class)
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @Bean
    public MyTelegramBot telegramBot(TelegramBotProperties properties, TelegramUserRepository repository) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MyTelegramBot bot = new MyTelegramBot(properties, repository);
            botsApi.registerBot(bot);
            return bot;
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при запуске Telegram бота", e);
        }
    }

}
