package ru.litu.notification_service.sheduler.tasks.dto;

import lombok.Data;
import java.util.List;

@Data
public class TaskResponseDto {
    private String status;
    private List<TaskDto> data;
}
