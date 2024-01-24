package ru.gb.myWebApplication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController // bean контроллер - обработчик запросов
@RequestMapping("/task") // - адрес вызова кнтроллера
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping // ничего не добавляем в адрес, потому вызов метода осуществляется по адресу контроллера("/task")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // @GetMapping запрос на получение данных
    // @PostMapping запрос на добавление данных (задач)
    // @PutMapping запрос на изменение данных
    // @DeleteMapping запрос на удаление данных
    @GetMapping("/{id}") // адрес вызова данного метода /task/id
    public Task getById(@PathVariable UUID id) { //@PathVariable указывает, что аргумент метода берется из пути(адреса)
        // в скобках можно указать название переменной @PathVariable(id), но в данном случае они совпадают, поэтому это делать не обязательно
        return taskService.getTask(id);
    }

    @PostMapping()
    public Task addTask(@RequestBody Task task) { // здесь аргумент берется из тела запроса, т.к. task в адресе не передать
        return taskService.addTask(task);
    }

    @PutMapping("/{id}")
    public Task ById(@PathVariable UUID id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
        taskService.deleteTask(id);
    }

    //postman приложение для отправки запросов или httpie.io
    //curl -консольное приложение отправки запросов
}
