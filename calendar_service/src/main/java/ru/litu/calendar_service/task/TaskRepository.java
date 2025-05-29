package ru.litu.calendar_service.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndDateBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);
    void deleteTasksByName(String name);
    List<Task> findTasksByName(String name);
}
