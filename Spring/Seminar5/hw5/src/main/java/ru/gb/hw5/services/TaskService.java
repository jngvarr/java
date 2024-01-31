package ru.gb.hw5.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.gb.hw5.repositories.TaskRepository;
import ru.gb.hw5.model.Task;
import ru.gb.hw5.model.TaskStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public void addTask(Task task) {
        repository.save(task);
    }

    public List<Task> showTasksByStatus(TaskStatus status) {
        return repository.showByStatus(status);
    }

    public void deleteTask(long id) {
        repository.deleteById(id);
    }

    public void changeTasksStatus(long id, TaskStatus status) {
        repository.changeStatus(id, status);
    }
}
