package ru.litu.calendar_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.litu.calendar_service.TestLoggerExtension;
import ru.litu.calendar_service.task.Task;
import ru.litu.calendar_service.task.TaskRepository;
import ru.litu.calendar_service.task.service.TaskService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(TestLoggerExtension.class)
public class TaskServiceTest {
    private static final Logger log = LoggerFactory.getLogger(TaskServiceTest.class);

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void save_ValidTask_Success() {
        log.info("Тест: сохранение валидной задачи - запущен");
        Task task = new Task("Тестовая задача", LocalDateTime.now().plusHours(1), "Описание", false, 1L);
        log.debug("Создана тестовая задача: {}", task);

        taskService.save(task);
        log.info("Задача успешно сохранена через сервис");

        verify(taskRepository).save(any(Task.class));
        verifyNoMoreInteractions(taskRepository);
        log.info("Проверка взаимодействий с репозиторием завершена");
        log.info("Тест успешно пройден");
    }

    @Test
    void save_NullTask_ThrowsException() {
        log.info("Тест: попытка сохранения null задачи - запущен");

        log.info("Проверка выброса исключения при сохранении null");
        assertThrows(IllegalArgumentException.class, () -> taskService.save(null));

        log.info("Тест завершен: исключение при null задаче выброшено корректно");
    }

    @Test
    void save_EmptyName_ThrowsException() {
        log.info("Тест: попытка сохранения задачи с пустым названием - запущен");
        Task task = new Task("", LocalDateTime.now(), "Описание", false, 1L);
        log.debug("Создана задача с пустым названием: {}", task);

        log.info("Проверка выброса исключения");
        assertThrows(IllegalArgumentException.class, () -> taskService.save(task));

        log.info("Тест завершен: исключение при пустом названии выброшено корректно");
    }

    @Test
    void findByDateAndUserId_ValidInput_ReturnsTasks() {
        log.info("Тест: поиск задач по дате и ID пользователя - запущен");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        List<Task> expectedTasks = Arrays.asList(
                new Task("Задача 1", start.plusHours(1), "Описание 1", false, 1L),
                new Task("Задача 2", start.plusHours(2), "Описание 2", true, 1L)
        );
        log.debug("Ожидаемый список задач: {}", expectedTasks);

        when(taskRepository.findByUserIdAndDateBetween(1L, start, end)).thenReturn(expectedTasks);
        log.info("Настроено поведение mock-репозитория");

        List<Task> result = taskService.findByDateAndUserId(1L, start, end);
        log.info("Получен результат из сервиса: {}", result);

        assertEquals(expectedTasks, result);
        verify(taskRepository).findByUserIdAndDateBetween(1L, start, end);
        log.info("Тест успешно завершен: возвращен корректный список задач");
    }

    @Test
    void getTask_ExistingId_ReturnsTask() {
        log.info("Тест: получение задачи по существующему ID - запущен");
        Task expectedTask = new Task("Тестовая задача", LocalDateTime.now(), "Описание", false, 1L);
        log.debug("Ожидаемая задача: {}", expectedTask);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(expectedTask));
        log.info("Настроено поведение mock-репозитория");

        Task result = taskService.getTask(1L);
        log.info("Получена задача из сервиса: {}", result);

        assertEquals(expectedTask, result);
        log.info("Тест успешно завершен: задача найдена");
    }

    @Test
    void getTask_NonExistingId_ThrowsException() {
        log.info("Тест: попытка получения несуществующей задачи - запущен");
        Optional<Task> res = Optional.empty();
        log.debug("Настроен пустой результат для репозитория");

        when(taskRepository.findById(1L)).thenReturn(res);
        log.info("Mock репозитория настроен на возврат пустого Optional");

        log.info("Проверка выброса исключения");
        assertThrows(IllegalArgumentException.class, () -> taskService.getTask(1L));

        log.info("Тест завершен: исключение при несуществующем ID выброшено корректно");
    }

    @Test
    void deleteTask_ValidId_DeletesTask() {
        log.info("Тест: удаление задачи по ID - запущен");

        taskService.deleteTask(1L);
        log.info("Вызван метод удаления задачи");

        verify(taskRepository).deleteById(1L);
        log.info("Проверено взаимодействие с репозиторием");
        log.info("Тест успешно завершен");
    }

    @Test
    void updateTaskName_ValidInput_UpdatesTask() {
        log.info("Тест: обновление названия задачи - запущен");
        Task existingTask = new Task("Старое название", LocalDateTime.now(), "Описание", false, 1L);
        log.debug("Исходная задача: {}", existingTask);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        log.info("Настроено поведение mock-репозитория");

        taskService.updateTaskName(1L, "Новое название");
        log.info("Название задачи обновлено");

        assertEquals("Новое название", existingTask.getName());
        verify(taskRepository).save(existingTask);
        log.info("Тест успешно завершен: название задачи обновлено");
    }

    @Test
    void updateIsComplete_ValidInput_UpdatesTask() {
        log.info("Тест: обновление статуса задачи - запущен");
        Task existingTask = new Task("Тестовая задача", LocalDateTime.now(), "Описание", false, 1L);
        log.debug("Исходная задача (статус не выполнен): {}", existingTask);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        log.info("Настроено поведение mock-репозитория");

        taskService.updateIsComplete(1L, true);
        log.info("Статус задачи обновлен");

        assertTrue(existingTask.isComplete());
        verify(taskRepository).save(existingTask);
        log.info("Тест успешно завершен: статус задачи изменен на 'выполнено'");
    }

    @Test
    void findAllActualTasks_ReturnsTasks() {
        log.info("Тест: получение актуальных задач - запущен");
        List<Task> expectedTasks = Arrays.asList(
                new Task("Задача 1", LocalDateTime.now().plusDays(1), "Описание 1", false, 1L),
                new Task("Задача 2", LocalDateTime.now().plusDays(2), "Описание 2", true, 1L)
        );
        log.debug("Ожидаемый список актуальных задач: {}", expectedTasks);

        when(taskRepository.findActualTasks()).thenReturn(expectedTasks);
        log.info("Настроено поведение mock-репозитория");

        List<Task> result = taskService.findAllActualTasks();
        log.info("Получен результат из сервиса: {}", result);

        assertEquals(expectedTasks, result);
        log.info("Тест успешно завершен: возвращен корректный список актуальных задач");
    }

}
