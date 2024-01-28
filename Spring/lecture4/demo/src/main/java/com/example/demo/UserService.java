package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public List<User> getAllUsers() {
        repository.save(new User(null, "igor", "qq@.qq.ru"));
        repository.save(new User(null, "jack", "jk@.qq.ru"));
        repository.save(new User(null, "john", "jn@.qq.ru"));
        return repository.findAll();
    }

    public User getUserById(Long id) {
        return repository.findById(id);
    }
}
