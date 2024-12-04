package ru.jngvarr.TGBot.service;

import org.springframework.transaction.annotation.Transactional;
import ru.jngvarr.TGBot.model.User;

public interface UserService {

    @Transactional
    void registerUser(User user);
}


