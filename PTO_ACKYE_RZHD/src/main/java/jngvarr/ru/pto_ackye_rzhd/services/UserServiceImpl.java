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

@Log4j2
@Service
@Getter
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public void registerUser(User user) {
        if (!repository.existsById(user.getUserId())) {
            repository.save(user);
        }
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


    public List<User> getUsers() {
        return repository.findAll();
    }

    @Transactional
    public User getUserById(Long id) {
        return repository.findById(id).orElse(null);
    }
}


