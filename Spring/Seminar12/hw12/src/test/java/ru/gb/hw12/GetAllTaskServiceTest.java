package ru.gb.hw12;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gb.hw12.model.Task;
import ru.gb.hw12.repositories.TaskRepository;
import ru.gb.hw12.services.TaskServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GetAllTaskServiceTest {
    @Mock
    public TaskRepository taskRepository;
    @InjectMocks
    public TaskServiceImpl taskService;

    @Test
    public void addTaskTest() {
        Task task = new Task();
    }
}
