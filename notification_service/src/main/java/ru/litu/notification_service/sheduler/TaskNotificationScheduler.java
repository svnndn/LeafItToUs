package ru.litu.notification_service.sheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.litu.notification_service.sheduler.tasks.Task;
import ru.litu.notification_service.sheduler.tasks.TaskRepository;
import ru.litu.notification_service.telegram.repository.TelegramUserRepository;
import ru.litu.notification_service.telegram.service.TelegramService;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskNotificationScheduler {

    private final TaskRepository taskRepository;
    private final TelegramUserRepository telegramUserRepository;
    private final TelegramService telegramService;

    @Scheduled(cron = "0 0/15 * * * *")
    public void checkAndSendNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusMinutes(7);
        LocalDateTime to = now.plusMinutes(7);

        List<Task> tasks = taskRepository.findTasksWithinRange(from,to);

        for (Task task : tasks) {
            telegramUserRepository.findByUserId(task.getUserId())
                    .ifPresent(user -> {
                        String message = String.format("Привет, уведомление от календаря ухода! Не забудь про напоминалку: %s", task.getName());
                        telegramService.sendMessage(user.getChatId(), message);
                    });
        }
    }
}
