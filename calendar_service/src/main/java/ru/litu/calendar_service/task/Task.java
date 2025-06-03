package ru.litu.calendar_service.task;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "task")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Task name cannot be blank")
    @Size(max = 100, message = "Task name must be less than 100 characters")
    @Column(name = "name")
    private String name;

    @FutureOrPresent(message = "Task date must be in the present or future")
    @Column(name = "date")
    private LocalDateTime date;

    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(name = "description")
    private String description;

    @Column(name = "is_complete")
    private boolean isComplete;

    @Min(value = 0, message = "User ID must be positive")
    @Column(name = "user_id")
    private long userId;
    public Task(String name, LocalDateTime date, String description, boolean isComplete, long userId) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.isComplete = isComplete;
        this.userId = userId;
    }
}
