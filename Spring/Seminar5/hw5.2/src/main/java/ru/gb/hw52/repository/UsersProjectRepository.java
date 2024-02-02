package ru.gb.hw52.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.hw52.model.UserToProject;

public interface UsersProjectRepository extends JpaRepository <UserToProject, Long> {

    boolean existsByUserIdAndProjectId(Long userId, Long projectId);

    UserToProject findByUserIdAndProjectId(Long userId, Long projectId);
}
