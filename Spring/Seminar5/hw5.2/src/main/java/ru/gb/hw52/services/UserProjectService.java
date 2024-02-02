package ru.gb.hw52.services;

import ru.gb.hw52.model.Project;
import ru.gb.hw52.model.User;

import java.util.List;


public interface UserProjectService {

    public List<Project> getProjectsByUserId(Long userId);

    public List<User> getUsersByProjectId(Long projectId);

    public User addUserToProject(Long userId, Long projectId);

    public void removeUserFromProject(Long userId, Long projectId);

}
