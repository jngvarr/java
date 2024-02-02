package ru.gb.hw52.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.hw52.model.UsersProject;

import java.util.Optional;

public interface UsersProjectRepository extends JpaRepository <UsersProject, Long> {

    boolean existsByUserIdAndProjectId(Long userId, Long projectId);

    UsersProject findByUserIdAndProjectId(Long userId, Long projectId);
}
