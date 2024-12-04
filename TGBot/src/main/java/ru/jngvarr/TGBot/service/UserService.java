package ru.jngvarr.TGBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.jngvarr.TGBot.model.User;
import ru.jngvarr.TGBot.model.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Transactional
    public void registerUser(Update update) {
        Long userId = update.getMessage().getChatId();
        Optional<User> optionalUser = repository.getByChatId(userId);
        if (optionalUser.isEmpty()) {
            User user = new User();
//        User user = repository.findById(userId)
//                .orElseGet(User::new);

            long chatId = update.getMessage().getChatId();
            Chat chat = update.getMessage().getChat();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUsername(chat.getUserName());
            user.setRegisteredAt(LocalDateTime.now());
        repository.save(user);
            log.info("User saved: " + user);
        }
    }
}

