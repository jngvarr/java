package ru.jngvarr.bot.services;

import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.jngvarr.bot.model.User;

public interface UserService {

    @Transactional
    void registerUser(User user);

    User createUser(Update update);
}


