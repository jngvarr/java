package ru.gb.hw5.controllers;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.gb.hw5.model.Task;
import ru.gb.hw5.model.TaskStatus;
import ru.gb.hw5.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    /**
     * Показать все задания
     * @return все задания из списка
     */
    @GetMapping
    public List<Task> showAllTasks() {
        return service.getAllTasks();
    }

    @PostMapping("/add")
    public void addTask(@RequestBody Task task){
        service.addTask(task);
    }
    @GetMapping("/status/{status}")
    public List<Task> showBTaskByStatus(@PathVariable TaskStatus status){
        return service.showTasksByStatus(status);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody TaskStatus status){
        service.changeTasksStatus(id, status);
    }
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable long id){
        service.deleteTask(id);
    }
}
