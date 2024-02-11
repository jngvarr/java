package ru.gb.myWebApplication.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая заметку
 */
@Entity
@Data
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    private LocalDateTime creationDate = LocalDateTime.now();
}
