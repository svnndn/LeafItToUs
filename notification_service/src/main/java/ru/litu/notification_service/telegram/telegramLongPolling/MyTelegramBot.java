package ru.litu.notification_service.telegram.telegramLongPolling;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.litu.notification_service.telegram.config.TelegramBotProperties;
import ru.litu.notification_service.telegram.model.TelegramUser;
import ru.litu.notification_service.telegram.repository.TelegramUserRepository;

@RequiredArgsConstructor
public class MyTelegramBot extends TelegramLongPollingBot {

    private final TelegramBotProperties properties;
    private final TelegramUserRepository telegramUserRepository;

    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (text.startsWith("/start")) {
                String[] parts = text.split(" ");
                if (parts.length > 1) {
                    Long userId = Long.parseLong(parts[1]);
                    telegramUserRepository.findByUserId(userId).ifPresentOrElse(
                            existing -> sendMessage(chatId, "Вы уже зарегистрированы!"),
                            () -> {
                                TelegramUser user = new TelegramUser();
                                user.setUserId(userId);
                                user.setChatId(chatId);
                                telegramUserRepository.save(user);
                                sendMessage(chatId, "Регистрация прошла успешно! 🌿");
                            });
                } else {
                    sendMessage(chatId, "Ошибка: Не указан userId.");
                }
            } else {
                sendMessage(chatId, "Да, братан, мы тут выращиваем");
            }
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
