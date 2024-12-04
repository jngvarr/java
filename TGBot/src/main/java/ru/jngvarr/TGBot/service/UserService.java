package ru.jngvarr.TGBot.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.jngvarr.TGBot.model.User;
import ru.jngvarr.TGBot.model.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void registerUser(Update update) {
        Long userId = update.getMessage().getChatId();

        User user = entityManager.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);

        if (user == null) {
            user = new User();
            long chatId = update.getMessage().getChatId();
            Chat chat = update.getMessage().getChat();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUsername(chat.getUserName());
            user.setRegisteredAt(LocalDateTime.now());
        }

        repository.save(user);
        log.info("User saved: " + user);
    }
}

