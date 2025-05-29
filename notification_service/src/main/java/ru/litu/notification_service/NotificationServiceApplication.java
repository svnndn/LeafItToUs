package ru.litu.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.litu.notification_service.telegram.config.TelegramBotProperties;
import ru.litu.notification_service.telegram.repository.TelegramUserRepository;
import ru.litu.notification_service.telegram.telegramLongPolling.MyTelegramBot;

@SpringBootApplication
@EntityScan(basePackages = {"ru.litu.notification_service.telegram.model",
        "ru.litu.notification_service.sheduler"})
@EnableConfigurationProperties(TelegramBotProperties.class)
@EnableScheduling
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
