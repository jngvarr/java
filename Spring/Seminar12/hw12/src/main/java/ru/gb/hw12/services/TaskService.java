package ru.gb.hw12.services;

import jakarta.transaction.Transactional;
import ru.gb.hw12.model.Task;
import ru.gb.hw12.model.TaskStatus;

import java.util.List;

/**
 * Сервис для управления задачами
 */
public interface TaskService {
    /**
     * Получить список всех задач.
     *
     * @return Список всех задач
     */
    List<Task> getAllTasks();

    /**
     * Получить задачу по идентификатору.
     *
     * @param id Идентификатор задачи
     * @return Задача с указанным идентификатором
     */
    Task get(long id);

    /**
     * Добавить новую задачу.
     *
     * @param task Новая задача для добавления
     * @return Добавленная задача
     */
    Task addTask(Task task);

    /**
     * Получить список задач по их статусу.
     *
     * @param status Статус задачи для фильтрации
     * @return Список задач с указанным статусом
     */
    List<Task> showTasksByStatus(TaskStatus status);

    /**
     * Удалить задачу по идентификатору.
     *
     * @param id Идентификатор задачи для удаления
     */
    void deleteTask(long id);

    /**
     * Изменить статус задачи по её идентификатору.
     *
     * @param id     Идентификатор задачи
     * @param status Новый статус задачи
     */
    @Transactional
    void changeTasksStatus(long id, TaskStatus status);
}