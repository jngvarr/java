package ru.gb.hw5.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String description;
    private TaskStatus status;
    private LocalDateTime creationDate = LocalDateTime.now();

//    public Task(String description, TaskStatus taskStatus, Date creationDate) {
//        this.description = description;
//        this.taskStatus = taskStatus;
//        this.creationDate = LocalDateTime.now();
//    }
}
