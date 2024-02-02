package ru.gb.hw52.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.hw52.model.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> getByUserId(Long userId);

    Project addUserById(Long projectId, Long userId);
    void deleteUserById(Long projectId, Long userId);
}
