package ru.gb.hw8.rest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.gb.hw8.rest.dto.UpdateStatusRequestDto;
import ru.gb.hw8.model.Task;
import ru.gb.hw8.model.TaskStatus;
import ru.gb.hw8.services.TaskService;

import java.util.List;

/**
 *  Контроллер, управляющий HTTP-запросами для операций с задачами
 */
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    /**
     * Показать все задания
     *
     * @return все задания из списка
     */
    @GetMapping
    public List<Task> showAllTasks() {
        return service.getAllTasks();
    }

    /**
     * Добавить новую задачу.
     *
     * @param task Новая задача, переданная в теле запроса
     * @return Добавленное задача
     */
    @PostMapping("/add")
    public Task addTask(@RequestBody Task task) {
        return service.addTask(task);
    }

    /**
     * Получить задачу по статусу.
     *
     * @param status Статус задачу для фильтрации
     * @return Список задач с указанным статусом
     */
    @GetMapping("/status/{status}")
    public List<Task> showTaskByStatus(@PathVariable TaskStatus status) {
        return service.showTasksByStatus(status);
    }

    /**
     * Обновить статус задачи по идентификатору.
     *
     * @param request Запрос на обновление статуса задачи
     * @return Обновленная задача
     */
    @PutMapping("/update")
    public Task updateStatus(@RequestBody UpdateStatusRequestDto request) {
        long id = request.getId();
        service.changeTasksStatus(id, request.getStatus());

        return service.get(id);
    }

    /**
     * Удалить задачу по идентификатору.
     *
     * @param id Идентификатор задачи для удаления
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable long id) {
        service.deleteTask(id);
    }
}