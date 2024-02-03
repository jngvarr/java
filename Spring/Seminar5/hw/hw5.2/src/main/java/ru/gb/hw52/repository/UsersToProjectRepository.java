package ru.gb.hw52.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.hw52.model.UserToProject;

import java.util.List;

public interface UsersToProjectRepository extends JpaRepository <UserToProject, Long> {
    UserToProject findByUserIdAndProjectId(long userId, long projectId);
    List<UserToProject> findByUserId(long userId);
    List<UserToProject> findByProjectId(long projectId);
}
