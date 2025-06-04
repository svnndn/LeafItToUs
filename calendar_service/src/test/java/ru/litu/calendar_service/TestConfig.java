package ru.litu.calendar_service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.litu.calendar_service.task.service.TaskService;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {
    @Bean
    public TaskService taskService() {
        return mock(TaskService.class);
    }
}
