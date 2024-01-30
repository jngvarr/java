package ru.gb.hw4.services;

import org.springframework.stereotype.Service;
import ru.gb.hw4.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления пользователями.
 */
@Service
public class UserService {
    private List<User> users = new ArrayList<>();

    /**
     * Получение списка пользователей.
     *
     * @return Список пользователей.
     */

    public List<User> getUsers() {
        return users;
    }

    /**
     * Добавление нового пользователя.
     *
     * @param user Новый пользователь.
     */
    public void addUser(User user) {
        users.add(user);
    }


}
