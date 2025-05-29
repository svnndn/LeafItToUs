package ru.litu.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import ru.litu.notification_service.config.TelegramBotProperties;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final TelegramBotProperties properties;

    public void sendMessage(String chatId, String text) {
        String url = String.format("%s/bot%s/sendMessage?chat_id=%s&text=%s",
                properties.getApiUrl(),
                properties.getToken(),
                chatId,
                text.replace(" ", "%20")); // Кодируем пробелы

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Ошибка при отправке сообщения в Telegram: " + response.getBody());
        }
    }
    public String generateRegistrationLink(Long userId) {
        String botUsername = properties.getUsername();
        return String.format("https://t.me/%s?start=%d", botUsername, userId);
    }
}
