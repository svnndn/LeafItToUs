package ru.litu.notification_service.telegram.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramMessageDto {
    private String chatId;
    private String message;
}
