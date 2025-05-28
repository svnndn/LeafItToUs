package ru.litu.calendar_service.task.service;

import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.litu.calendar_service.task.Task;
import ru.litu.calendar_service.task.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public void save(Task task) {
        taskRepository.save(task);
    }

    public List<Task> findByDateAndUserId(int date, long userId) {
        // in testing, date=20220228, calendar=1
        return taskRepository.findByDateAndUserId(date, userId);
    }

    public Task getTask(long id) {
        return taskRepository.findById(id).get();
    }

    public void deleteTask(long id) {
        taskRepository.deleteById(id);
    }

    public void updateTaskName(long id, String name) {
        Task task = this.getTask(id);
        task.setName(name);
        this.save(task);
        // according to https://stackoverflow.com/questions/11881479/how-do-i-update-an-entity-using-spring-data-jpa
        // calling save() on an object with predefined id will update the corresponding database record rather than insert a new one
    }

    public void updateIsComplete(long id, boolean isComplete) {
        Task task = this.getTask(id);
        task.setComplete(isComplete);
        this.save(task);
    }

    public void deleteTasksByName(String name) {
        taskRepository.deleteTasksByName(name);
    }

    public List<Task> findTasksByName(String name) {
        return taskRepository.findTasksByName(name);
    }
}
