package jngvarr.ru.pto_ackye_rzhd.domain.services;

import jngvarr.ru.pto_ackye_rzhd.domain.entities.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserService {

//    @Transactional
    User registerUser(User user);

    User createUser(Update update);

    User checkUser(Update update);
}



