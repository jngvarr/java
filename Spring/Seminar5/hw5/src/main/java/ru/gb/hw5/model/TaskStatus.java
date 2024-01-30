package ru.gb.hw5.model;

public enum TaskStatus {
    DONE("Завершена"),
    IN_PROGRESS("В процесс"),
    NOT_STARTED("Не начата");

    TaskStatus(String status) {
    }
}
