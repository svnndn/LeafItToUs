package ru.litu.calendar_service.task;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "name")
    private String name;

    @Column(name = "date")
    private int date;

    @Column(name = "description")
    private String description;

    @Column(name = "is_complete")
    private boolean isComplete;

    @Column(name = "user_id")
    private long userId;

    public Task(String name, int date, String description, boolean isComplete, long userId) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.isComplete = isComplete;
        this.userId = userId;
    }
}
