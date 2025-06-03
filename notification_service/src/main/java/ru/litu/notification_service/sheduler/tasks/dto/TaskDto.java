package ru.litu.notification_service.sheduler.tasks.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private boolean complete;
    private Long userId;
    private LocalDateTime date;
}
