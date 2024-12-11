package ru.jngvarr.bot.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.jngvarr.bot.model.User;
import ru.jngvarr.bot.model.UserRepository;

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
}


