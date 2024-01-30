package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис, предоставляющий функциональность для работы с пользователями.
 */
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Получение списка всех пользователей.
     *
     * @return Список пользователей.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Сохранение нового пользователя.
     *
     * @param user Новый пользователь для сохранения.
     * @return Сохраненный пользователь.
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    /**
     * Удаление пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя для удаления.
     */
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }
    /**
     * Получение пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Найденный пользователь.
     */
    public User getOne(int id) {
        return userRepository.getOne(id);
    }
    /**
     * Обновление данных пользователя.
     *
     * @param id   Идентификатор пользователя для обновления.
     * @param user Новые параметры для обновленныя данных пользователя.
     */
    public void updateUser(int id, User user) {
        userRepository.updateUser(id, user);
    }


}
