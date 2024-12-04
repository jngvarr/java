package ru.jngvarr.TGBot.service;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.jngvarr.TGBot.model.User;
import ru.jngvarr.TGBot.model.UserRepository;

import java.time.LocalDateTime;

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


