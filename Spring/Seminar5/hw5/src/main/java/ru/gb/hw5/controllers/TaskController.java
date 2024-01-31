package ru.gb.hw5.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.gb.hw5.model.Task;
import ru.gb.hw5.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private TaskService service;

    /**
     * Показать все задания
     * @return все задания из списка
     */
    @GetMapping()
    public List<Task> showAllTasks() {
        return service.getAllTasks();
    }

//    @PostMapping("/add")
//    public void addTask(@RequestBody Task task){
//        service.addTask(task);
//    }
}
