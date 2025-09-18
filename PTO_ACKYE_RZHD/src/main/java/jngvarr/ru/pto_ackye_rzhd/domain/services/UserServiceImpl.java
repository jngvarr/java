package jngvarr.ru.pto_ackye_rzhd.domain.services;

import jakarta.transaction.Transactional;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.User;
import jngvarr.ru.pto_ackye_rzhd.domain.repositories.UserRepository;
import jngvarr.ru.pto_ackye_rzhd.telegram.TBotMessageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@Getter
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final TBotMessageService messageService;

    @Override
//    @Transactional
    public User registerUser(User user) {
        if (!repository.existsById(user.getUserId())) {
           return repository.save(user);
        }
        return user;
    }


    @Override
    public User createUser(Update update) {
        var message = update.getMessage();
        var from = message.getFrom(); // именно этот объект содержит пользователя

        if (from == null) {
            throw new IllegalStateException("Невозможно определить пользователя: update.getMessage().getFrom() == null");
        }

        User user = new User();
        user.setUserId(message.getFrom().getId()); // сохраняем ID чата (может быть userId или groupId)
        from.getFirstName();
        user.setFirstName(from.getFirstName()); // защита от null
        user.setLastName(from.getLastName() != null ? from.getLastName() : "");
        user.setUsername(from.getUserName());
        user.setRegisteredAt(LocalDateTime.now());
        user.setAccepted(false);
        return user;
    }

    @Override
    public User checkUser(Update update) {
        long userId = 0;
        long chatId = 0;
        if (update.hasMessage()) {
            messageService.forwardMessage(update.getMessage());
            userId = update.getMessage().getFrom().getId();
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
        }

        User user = getUserById(userId);
        String incomingText = update.hasMessage() && update.getMessage().hasText()
                ? update.getMessage().getText()
                : "";

        if (user == null && "/register".equals(incomingText)) {
            user = registerUser(update);
            messageService.sendMessage(chatId, userId, "Пользователь успешно зарегистрирован.");
        } else if ("/register".equals(incomingText)) {
            messageService.sendMessage(chatId, userId, "Вы уже зарегистрированы!!!");
        } else if (user == null || !user.isAccepted()) {
            messageService.sendMessage(chatId, userId, "Пожалуйста, пройдите регистрацию и дождитесь валидации администратора.");
        }
        return user;
    }

    public User registerUser(Update update) {
        User newUser = createUser(update);
       return registerUser(newUser);
    }

    public List<User> getUsers() {
        return repository.findAll();
    }

    public Long getCurrentuserId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }

        return null;
    }

    @Transactional
    public User getUserById(Long id) {
        return repository.findById(id).orElse(null);
    }
}


