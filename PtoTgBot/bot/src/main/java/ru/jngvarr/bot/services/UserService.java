package ru.jngvarr.bot.services;

import org.springframework.transaction.annotation.Transactional;
import ru.jngvarr.bot.model.User;

public interface UserService {

    @Transactional
    void registerUser(User user);
}


