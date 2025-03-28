package ru.gb.myWebApplication;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final List<Task> tasks = new ArrayList<>();

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Task getTask(UUID uuid) {
        return tasks.stream().filter(t -> t.getId().equals(uuid)).findFirst().orElse(null);
    }

    public Task addTask(Task task) {
        tasks.add(task);
        return task;
    }

    public void deleteTask(UUID uuid) {
        tasks.removeIf(task -> task.getId().equals(uuid));
    }

    public Task updateTask(UUID uuid, Task task) {
        Task task1 = getTask(uuid);
        if (task1 != null) {
            task1.setDescription(task.getDescription());
            task1.setName(task.getName());
            task1.setStatus(task.getStatus());
            task1.setCompetitionTime(task.getCompetitionTime());
        }
        return task1;
    }
}
