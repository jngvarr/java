package ru.jngvarr.clientmanagement.services;

import dao.entities.people.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jngvarr.clientmanagement.repositories.UserRepository;

@Data
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public User getUserByUserName(String userName) {
        return userRepository.getUserByUserName(userName);
    }
}
