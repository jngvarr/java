package ru.gb.hw5.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
/**
 * Перечисление, представляющее возможные статусы задачи.
 */
public enum TaskStatus {
    DONE,
    IN_PROGRESS,
    NOT_STARTED
}
