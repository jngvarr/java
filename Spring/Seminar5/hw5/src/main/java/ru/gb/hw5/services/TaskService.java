package ru.gb.hw5.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.hw5.repositories.TaskRepository;
import ru.gb.hw5.model.Task;
import ru.gb.hw5.model.TaskStatus;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    TaskRepository repository;

    public List<Task> getAllTasks(Task task) {
        return repository.findAll();
    }

    public void addTask(Task task) {
        repository.save(task);
    }

    public List<Task> showTasksByStatus(TaskStatus status) {
        return repository.showByStatus(status);
    }

    public void changeTasksStatus(Long id, TaskStatus status) {
        repository.changeStatus(id, status);
    }
    public void deleteTask(Long id){
        repository.deleteById(id);
    }

    public void deleteTask(String name) {
        repository.deleteByName(name);
    }
}
