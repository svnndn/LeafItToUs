package ru.litu.notification_service.sheduler.tasks;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime dateTime; // Используем дату и время в одном поле

    private String description;

    private Boolean isComplete;

    private Long userId;
}
