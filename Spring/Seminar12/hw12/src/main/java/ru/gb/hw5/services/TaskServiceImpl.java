package ru.gb.hw5.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.hw5.aspect.TrackUserRequest;
import ru.gb.hw5.model.Task;
import ru.gb.hw5.model.TaskStatus;
import ru.gb.hw5.repositories.TaskRepository;

import java.util.List;

/**
 * Реализация сервиса для управления задачами
 */
@Service
@TrackUserRequest
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    /**
     * Получить список всех задач.
     *
     * @return Список всех задач
     */
    @Override
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    /**
     * Получить задачу по идентификатору.
     *
     * @param id Идентификатор задачи
     * @return Задача с указанным идентификатором
     */
    @Override
    public Task get(long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Добавить новую задачу.
     *
     * @param task Новая задача для добавления
     * @return Добавленная задача
     */
    @Override
    public Task addTask(Task task) {
        return repository.save(task);
    }

    /**
     * Получить список задач по их статусу.
     *
     * @param status Статус задачи для фильтрации
     * @return Список задач с указанным статусом
     */
    @Override
    public List<Task> showTasksByStatus(TaskStatus status) {
        return repository.showByStatus(status);
    }

    /**
     * Удалить задачу по идентификатору.
     *
     * @param id Идентификатор задачи для удаления
     */
    @Override
    public void deleteTask(long id) {
        repository.deleteById(id);
    }

    /**
     * Изменить статус задачи по её идентификатору.
     *
     * @param id     Идентификатор задачи
     * @param status Новый статус задачи
     */
    @Override
    @Transactional
    public void changeTasksStatus(long id, TaskStatus status) {
        repository.changeStatus(id, status);
    }
}