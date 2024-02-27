package ru.gb.hw12.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.gb.hw12.model.Task;
import ru.gb.hw12.model.TaskStatus;

import java.util.List;
/**
 * Репозиторий для работы с задачами в базе данных.
 */

    public interface TaskRepository extends JpaRepository<Task, Long> {
        @Query(value = "SELECT task FROM Task task WHERE task.status = :status")
        List<Task> showByStatus(TaskStatus status);

        @Modifying
        @Query("UPDATE Task SET status = :status WHERE id = :id")
        void changeStatus(long id, TaskStatus status);
    }