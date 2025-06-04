package ru.litu.calendar_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.litu.calendar_service.dto.ServiceResponseDto;
import ru.litu.calendar_service.service.TaskServiceTest;
import ru.litu.calendar_service.task.Task;
import ru.litu.calendar_service.task.TaskController;
import ru.litu.calendar_service.task.service.TaskService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskControllerTest {
    private static final Logger log = LoggerFactory.getLogger(TaskControllerTest.class);

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @Test
    void displayTasksByClick_ValidInput_ReturnsTasks() {
        log.info("Тест: отображение задач по клику - запущен");

        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 23, 59);
        List<Task> tasks = Arrays.asList(
                new Task("Task 1", startTime.plusHours(1), "Desc 1", false, 1),
                new Task("Task 2", startTime.plusHours(2), "Desc 2", true, 1)
        );
        log.debug("Подготовлен тестовый список задач: {}", tasks);

        log.info("Настройка mock сервиса");
        when(taskService.findByDateAndUserId(1, startTime, endTime)).thenReturn(tasks);

        log.info("Вызов метода контроллера с датой 20230101 и userId=1");
        ResponseEntity<Object> response = taskController.displayTasksByClick(20230101, 1);

        log.info("Проверка статуса ответа");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ServiceResponseDto<?> responseBody = (ServiceResponseDto<?>) response.getBody();
        assertNotNull(responseBody, "Тело ответа не должно быть null");
        assertEquals("success", responseBody.getStatus(), "Статус должен быть 'success'");

        log.info("Тест успешно завершен");
    }

    @Test
    void displayDetail_ExistingTask_ReturnsTask() {
        log.info("Тест: отображение деталей задачи - запущен");

        Task task = new Task("Task", LocalDateTime.now(), "Desc", false, 1L);
        log.debug("Подготовлена тестовая задача: {}", task);

        log.info("Настройка mock сервиса");
        when(taskService.getTask(1L)).thenReturn(task);

        log.info("Вызов метода контроллера с taskId=1");
        ResponseEntity<Object> response = taskController.displayDetail(1L);

        log.info("Проверка ответа");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ServiceResponseDto<?> responseBody = (ServiceResponseDto<?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.getStatus());
        assertEquals(task, responseBody.getData());

        log.info("Тест успешно завершен: задача найдена");
    }

    @Test
    void save_ForOneDay_CreatesOneTask() {
        log.info("Тест: сохранение задачи на один день - запущен");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Test Task");
        request.addParameter("description", "Test Desc");
        request.addParameter("type", "forOneDay");
        request.addParameter("hours", "12");
        request.addParameter("minutes", "30");
        request.addParameter("date", "1 1 2023");
        log.debug("Подготовлен запрос с параметрами: name=Test Task, type=forOneDay");

        log.info("Вызов метода сохранения");
        String result = taskController.save(1L, request, null);

        log.info("Проверка результата");
        assertEquals("redirect:/calendar/1", result);
        verify(taskService, times(1)).save(any(Task.class));

        log.info("Тест успешно завершен: создана одна задача");
    }

    @Test
    void save_ForWeek_CreatesSevenTasks() {
        log.info("Тест: сохранение задач на неделю - запущен");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Test Task");
        request.addParameter("description", "Test Desc");
        request.addParameter("type", "forWeek");
        request.addParameter("hours", "12");
        request.addParameter("minutes", "30");
        request.addParameter("date", "1 1 2023");
        log.debug("Подготовлен запрос с параметрами: name=Test Task, type=forWeek");

        log.info("Вызов метода сохранения");
        String result = taskController.save(1L, request, null);

        log.info("Проверка результата");
        assertEquals("redirect:/calendar/1", result);
        verify(taskService, times(7)).save(any(Task.class));

        log.info("Тест успешно завершен: создано 7 задач");
    }

    @Test
    void deleteTask_ValidId_DeletesTask() {
        log.info("Тест: удаление задачи - запущен");

        log.info("Вызов метода удаления с taskId=1 и userId=1");
        String result = taskController.deleteTask(1L, 1L);

        log.info("Проверка результата");
        assertEquals("redirect:/calendar/1", result);
        verify(taskService).deleteTask(1L);

        log.info("Тест успешно завершен: задача удалена");
    }

    @Test
    void completeTask_TogglesCompletion() {
        log.info("Тест: изменение статуса выполнения задачи - запущен");

        log.info("Вызов метода с taskId=1, isComplete=false, userId=1");
        String result = taskController.completeTask(1L, false, 1L);

        log.info("Проверка результата");
        assertEquals("redirect:/calendar/1", result);
        verify(taskService).updateIsComplete(1L, true);

        log.info("Тест успешно завершен: статус задачи изменен");
    }

    @Test
    void editTask_ValidInput_UpdatesTask() {
        log.info("Тест: редактирование задачи - запущен");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "New Name");
        request.addParameter("description", "New Desc");
        log.debug("Подготовлен запрос с новыми параметрами: name=New Name");

        Task existingTask = new Task("Old Name", LocalDateTime.now(), "Old Desc", false, 1L);
        log.debug("Исходная задача: {}", existingTask);

        log.info("Настройка mock сервиса");
        when(taskService.getTask(1L)).thenReturn(existingTask);

        log.info("Вызов метода редактирования");
        String result = taskController.editTask(1L, 1L, request);

        log.info("Проверка результата");
        assertEquals("redirect:/calendar/1", result);
        assertEquals("New Name", existingTask.getName());
        assertEquals("New Desc", existingTask.getDescription());
        verify(taskService).save(existingTask);

        log.info("Тест успешно завершен: задача обновлена");
    }

    @Test
    void getAllActualTasks_ReturnsTasks() {
        log.info("Тест: получение всех актуальных задач - запущен");

        List<Task> tasks = Arrays.asList(
                new Task("Task 1", LocalDateTime.now().plusDays(1), "Desc 1", false, 1L),
                new Task("Task 2", LocalDateTime.now().plusDays(2), "Desc 2", true, 1L)
        );
        log.debug("Подготовлен тестовый список задач: {}", tasks);

        log.info("Настройка mock сервиса");
        when(taskService.findAllActualTasks()).thenReturn(tasks);

        log.info("Вызов метода получения задач");
        ResponseEntity<Object> response = taskController.getAllActualTasks();

        log.info("Проверка ответа");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ServiceResponseDto<?> responseBody = (ServiceResponseDto<?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.getStatus());
        assertEquals(tasks, responseBody.getData());
        assertEquals(Arrays.asList(0, 1), responseBody.getIsCompleteList());

        log.info("Тест успешно завершен: задачи получены");
    }
}
