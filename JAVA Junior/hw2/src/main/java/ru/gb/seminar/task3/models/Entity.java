package ru.gb.seminar.task3.models;


import ru.gb.seminar.task3.Column;

import java.util.UUID;

@ru.gb.seminar.task3.Entity
public class Entity {

    @Column(name = "id", primaryKey = true)
    private UUID id;

}
