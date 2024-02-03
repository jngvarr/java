package ru.gb.hw52.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.hw52.model.Project;
import ru.gb.hw52.model.User;
import ru.gb.hw52.model.UserToProject;
import ru.gb.hw52.repository.ProjectRepository;
import ru.gb.hw52.repository.UserRepository;
import ru.gb.hw52.repository.UsersToProjectRepository;

import java.util.List;
import java.util.stream.Collectors;
/**
 * Реализация сервиса для управления DB
 */
@Service
@RequiredArgsConstructor
public class UserProjectServiceImpl implements UserProjectService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UsersToProjectRepository usersToProjectRepository;

    /**
     * Получение списка пользователей, связанных с определенным проектом
     *
     * @param projectId уникальный идентификатор проекта
     * @return список пользователей
     */
    @Override
    public List<User> getUsersByProjectId(long projectId) {
        List<UserToProject> relations = usersToProjectRepository.findByProjectId(projectId);
        List<Long> ids = relations.stream()
                .map(UserToProject::getUserId)
                .collect(Collectors.toList());
        return userRepository.findAllById(ids);
    }

    /**
     * Получение списка проектов, связанных с определенным пользователем
     *
     * @param userId уникальный идентификатор пользователя
     * @return список проектов
     */
    public List<Project> getProjectsByUserId(long userId) {
        List<UserToProject> relations = usersToProjectRepository.findByUserId(userId);
        List<Long> ids = relations.stream()
                .map(UserToProject::getUserId)
                .collect(Collectors.toList());
        return projectRepository.findAllById(ids);
    }

    /**
     * Добавление пользователя проекту
     *
     * @param userId    уникальный идентификатор, добавляемого пользователя
     * @param projectId уникальный идентификатор, проекта для добавления пользователя
     */
    public void addUserToProject(long userId, long projectId) {
        usersToProjectRepository.save(new UserToProject(userId, projectId));
    }

    /**
     * Удаление пользователя из проекта
     *
     * @param userId    уникальный идентификатор, удаляемого пользователя
     * @param projectId уникальный идентификатор проекта для добавления пользователя
     * @return
     */
    public void removeUserFromProject(long userId, long projectId) {
        UserToProject record = usersToProjectRepository.findByUserIdAndProjectId(userId, projectId);
        if (record != null) {
            usersToProjectRepository.deleteById(record.getId());
        }
    }
}