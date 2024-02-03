package ru.gb.hw52.services;

import ru.gb.hw52.model.Project;
import ru.gb.hw52.model.User;

import java.util.List;

/**
 * Сервис для управления БД
 */

public interface UserProjectService {
    /**
     * Получение списка проектов, связанных с определенным пользователем
     *
     * @param userId уникальный идентификатор пользователя
     * @return список проектов
     */
    List<Project> getProjectsByUserId(long userId);

    /**
     * Получение списка пользователей, связанных с определенным проектом
     *
     * @param projectId уникальный идентификатор проекта
     * @return список пользователей
     */
    List<User> getUsersByProjectId(long projectId);

    /**
     * Добавление пользователя проекту
     *
     * @param userId    уникальный идентификатор, добавляемого пользователя
     * @param projectId уникальный идентификатор, проекта для добавления пользователя
     */
    void addUserToProject(long userId, long projectId);

    /**
     * Удаление пользователя из проекта
     *
     * @param userId    уникальный идентификатор, удаляемого пользователя
     * @param projectId уникальный идентификатор проекта для добавления пользователя
     * @return
     */
    void removeUserFromProject(long userId, long projectId);

}
