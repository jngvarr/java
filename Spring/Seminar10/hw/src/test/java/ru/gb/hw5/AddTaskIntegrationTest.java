package ru.gb.hw5;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.gb.hw5.model.Task;
import ru.gb.hw5.repositories.TaskRepository;
import ru.gb.hw5.services.TaskServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Интеграционный тест метода addTask
 */
@SpringBootTest
public class AddTaskIntegrationTest {
    @MockBean
    public TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl taskService;

    @Test
    public void addTaskTestHappyFlow() {
        Task task = new Task();
        task.setId(1L);
        task.setDescription("task for test");

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task addedTask = taskService.addTask(task);

        verify(taskRepository).save(task);

        assert addedTask != null;
        assert addedTask.getId() == 1L;
        assert addedTask.getDescription().equals("task for test");
    }
}
