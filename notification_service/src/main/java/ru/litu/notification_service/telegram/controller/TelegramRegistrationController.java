package ru.litu.notification_service.telegram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litu.notification_service.telegram.service.TelegramService;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class TelegramRegistrationController {

    private final TelegramService telegramService;

    @GetMapping("/registration-link/{userId}")
    public ResponseEntity<Void> redirectToTelegram(@PathVariable Long userId) {
        String url = telegramService.generateRegistrationLink(userId);
        return ResponseEntity.status(302).header("Location", url).build();
    }
}
