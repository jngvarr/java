package ru.jngvarr.TGBot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.glassfish.grizzly.http.util.TimeStamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

//    @Version
//    private int version; // Добавлено для управления конфликтами
}
