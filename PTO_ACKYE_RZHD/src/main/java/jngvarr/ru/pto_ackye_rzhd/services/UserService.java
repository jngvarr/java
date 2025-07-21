package jngvarr.ru.pto_ackye_rzhd.services;

import jngvarr.ru.pto_ackye_rzhd.entities.User;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserService {

//    @Transactional
    void registerUser(User user);

    User createUser(Update update);
}



