package ru.litu.notification_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litu.notification_service.model.TelegramUser;
import ru.litu.notification_service.repository.TelegramUserRepository;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class TelegramUserController {

    private final TelegramUserRepository telegramUserRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam Long userId, @RequestParam String chatId) {
        // Проверим, существует ли пользователь
        if (telegramUserRepository.findByUserId(userId).isPresent()) {
            return ResponseEntity.badRequest().body("Пользователь с таким userId уже зарегистрирован.");
        }

        TelegramUser user = new TelegramUser();
        user.setUserId(userId);
        user.setChatId(chatId);
        telegramUserRepository.save(user);

        return ResponseEntity.ok("Пользователь зарегистрирован.");
    }

    @GetMapping("/getChatId")
    public ResponseEntity<String> getChatId(@RequestParam Long userId) {
        return telegramUserRepository.findByUserId(userId)
                .map(user -> ResponseEntity.ok(user.getChatId()))
                .orElse(ResponseEntity.notFound().build());
    }
}
