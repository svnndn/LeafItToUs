package ru.litu.notification_service.sheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.litu.notification_service.sheduler.tasks.dto.TaskDto;
import ru.litu.notification_service.sheduler.tasks.dto.TaskResponseDto;
import ru.litu.notification_service.telegram.repository.TelegramUserRepository;
import ru.litu.notification_service.telegram.service.TelegramService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskNotificationScheduler {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TelegramUserRepository telegramUserRepository;
    private final TelegramService telegramService;

    @Value("${calendar-url}")
    private String calendarServiceUrl;

    @Scheduled(cron = "* 0/2 * * * *")
    public void checkAndSendNotifications() {
        System.out.println("я пошел");
        TaskResponseDto response = restTemplate.getForObject(calendarServiceUrl, TaskResponseDto.class);
        System.out.println(response.toString());
        if (response == null || response.getData() == null) {
            System.out.println("Не удалось получить задачи или список задач пуст");
            return;
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        System.out.println(now);
        LocalDateTime to = now.plusMinutes(15);

        List<TaskDto> tasks = response.getData();

        for (TaskDto task : tasks) {
            if (!task.isComplete() && task.getDate() != null
                    && task.getDate().isAfter(now) && task.getDate().isBefore(to)) {
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
