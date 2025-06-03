package ru.litu.calendar_service.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndDateBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);
    void deleteTasksByName(String name);
    List<Task> findTasksByName(String name);
    @Query(value = "SELECT * FROM task WHERE date > NOW() ORDER BY date ASC",
            nativeQuery = true)
    List<Task> findActualTasks();
}
