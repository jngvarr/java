package com.example.demo.model;

import lombok.Data;

/**
 * Класс, представляющий сущность пользователя.
 */
@Data
public class User {
    private int id;

    private String firstName;

    private String lastName;
}
