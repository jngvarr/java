package jngvarr.ru.pto_ackye_rzhd.services;

import jakarta.transaction.Transactional;
import jngvarr.ru.pto_ackye_rzhd.entities.User;
import jngvarr.ru.pto_ackye_rzhd.repositories.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@Getter
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public void registerUser(User user) {
        repository.save(user);
    }
    @Override
    public User createUser(Update update) {
        var chatId = update.getMessage().getChatId();
        var chat = update.getMessage().getChat();

        User user = new User();
        user.setChatId(chatId);
        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setUsername(chat.getUserName());
        user.setRegisteredAt(LocalDateTime.now());
        user.setAccepted(false);
        return user;
    }

    public List<User> getUsers(){
        return repository.findAll();
    }
    @Transactional
    public User getUserById(Long id){
        return repository.findById(id).orElse(null);
    }
}


