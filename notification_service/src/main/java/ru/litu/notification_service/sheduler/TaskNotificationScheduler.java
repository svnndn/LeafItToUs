package ru.litu.notification_service.sheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.litu.notification_service.sheduler.tasks.dto.TaskDto;
import ru.litu.notification_service.sheduler.tasks.dto.TaskResponseDto;
import ru.litu.notification_service.telegram.repository.TelegramUserRepository;
import ru.litu.notification_service.telegram.service.TelegramService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskNotificationScheduler {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TelegramUserRepository telegramUserRepository;
    private final TelegramService telegramService;

    private final String taskServiceUrl = "http://localhost:8083/actual";

    @Scheduled(cron = "0 0/15 * * * *")
    public void checkAndSendNotifications() {
        TaskResponseDto response = restTemplate.getForObject(taskServiceUrl, TaskResponseDto.class);
        if (response == null || response.getData() == null) {
            System.out.println("Не удалось получить задачи или список задач пуст");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusMinutes(7);
        LocalDateTime to = now.plusMinutes(7);

        List<TaskDto> tasks = response.getData();

        for (TaskDto task : tasks) {
            if (!task.isComplete() && task.getDate() != null
                    && !task.getDate().isBefore(from) && !task.getDate().isAfter(to)) {
                telegramUserRepository.findByUserId(task.getUserId())
                        .ifPresent(user -> {
                            String message = String.format(
                                    "Привет, уведомление от календаря ухода! Не забудь про напоминалку: %s",
                                    task.getName());
                            telegramService.sendMessage(user.getChatId(), message);
                        });
            }
        }
    }
}
