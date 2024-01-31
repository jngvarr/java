package ru.gb.hw5.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TaskStatus {
    DONE("DONE"),
    IN_PROGRESS("IN_PROGRESS"),
    NOT_STARTED("NOT_STARTED");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static TaskStatus fromValue(String value) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Неизвестное значение enum: " + value);
    }
}
