package ru.gb.hw52.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.hw52.model.Project;
import ru.gb.hw52.model.User;
import ru.gb.hw52.repository.ProjectRepository;
import ru.gb.hw52.repository.UserRepository;
import ru.gb.hw52.repository.UsersProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProjectServiceImpl implements UserProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UsersProjectRepository usersProjectRepository;

    /**
     * Получение списка пользователей, связанных с определенным проектом
     *
     * @param projectId уникальный идентификатор проекта
     * @return список пользователей
     */
    public List<User> getUsersByProjectId(Long projectId) {
        return userRepository.getByProjectId(projectId);
    }

    /**
     * Получение списка проектов, связанных с определенным пользователем
     *
     * @param userId уникальный идентификатор пользователя
     * @return список проектов
     */
    public List<Project> getProjectsByUserId(Long userId) {
        return projectRepository.getByUserId(userId);
    }

    /**
     * Добавление пользователя проекту
     *
     * @param userId    уникальный идентификатор, добавляемого пользователя
     * @param projectId уникальный идентификатор, проекта для добавление пользователя
     * @return
     */
    public User addUserToProject(Long userId, Long projectId) {
        projectRepository.addUserById(projectId, userId);
        return userRepository.getReferenceById(userId);
    }

    /**
     * удаление пользователя из проекта
     *
     * @param userId    уникальный идентификатор, удаляемого пользователя
     * @param projectId уникальный идентификатор, проекта для добавление пользователя
     */
    public void removeUserFromProject(Long userId, Long projectId) {
        projectRepository.deleteUserById(projectId, userId);
    }

}
