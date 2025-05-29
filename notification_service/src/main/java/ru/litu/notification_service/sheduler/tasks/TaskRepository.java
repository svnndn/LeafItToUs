package ru.litu.notification_service.sheduler.tasks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDateTimeBetween(LocalDateTime from, LocalDateTime to);
    @Query("SELECT t FROM Task t WHERE t.dateTime BETWEEN :from AND :to AND t.isComplete = false")
    List<Task> findTasksWithinRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
