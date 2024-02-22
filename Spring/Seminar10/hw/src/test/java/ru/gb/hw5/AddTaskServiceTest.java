package ru.gb.hw5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gb.hw5.exceptions.TaskNotFoundException;
import ru.gb.hw5.model.Task;
import ru.gb.hw5.repositories.TaskRepository;
import ru.gb.hw5.services.TaskServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AddTaskServiceTest {
    @Mock
    public TaskRepository taskRepository;
    @InjectMocks
    public TaskServiceImpl taskService;

    @Test
    public void addTaskTestHappyFlow() {
        // pre Создаем экземпляр задачи, которую мы хотим добавить
        Task task = new Task();
        task.setId(1L);
        task.setDescription("task for test");
//        given(taskRepository);

        // Когда вызывается метод save у taskRepository с любым объектом Task, то возвращаем этот же объект
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // action Вызываем метод addTask у taskService

        Task addedTask = taskService.addTask(task);

        verify(taskRepository).save(task);

        // Проверяем, что добавленная задача соответствует той, которую мы ожидаем
        assert addedTask != null;
        assert addedTask.getId() == 1L;
        assert addedTask.getDescription().equals("task for test");
    }

    @Test
    public void addTaskTestIllegalArgumentFlow() {
        // Создаем экземпляр задачи с недопустимыми данными (например, id = 0)
        Task task = new Task();
        task.setId(0L); // Недопустимый идентификатор

        // Когда вызывается метод save у repository с любым объектом Task, выбрасываем исключение
        when(taskRepository.save(any(Task.class))).thenThrow(IllegalArgumentException.class);

        // Проверяем, что при вызове метода addTask выбрасывается исключение IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> taskService.addTask(task));
    }

}
