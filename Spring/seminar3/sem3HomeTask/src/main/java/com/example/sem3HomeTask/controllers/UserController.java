package com.example.sem3HomeTask.controllers;

import com.example.sem3HomeTask.domain.User;
import com.example.sem3HomeTask.services.RegistrationService;
import com.example.sem3HomeTask.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")//localhost:8080/user
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService service;

    @GetMapping
    public List<User> userList() {
        return service.getDataProcessingService().getRepository().getUsers();
    }

    @PostMapping("/body")
    public String userAddFromBody(@RequestBody User user) {
        service.getDataProcessingService().getRepository().getUsers().add(user);
        return "User added from body!";
    }

    /**
     * Создание пользователя из параметров HTTP запроса
     * @param name имя пользователя
     * @param age возраст
     * @param email электронный адрес
     * @return
     */
    @PostMapping("/params")
    public ResponseEntity<User> userAddFromParams(
            @RequestParam String name,
            @RequestParam int age,
            @RequestParam String email) {
        User user = userService.createUser(name, age, email);
        service.getDataProcessingService().getRepository().getUsers().add(user);
        return ResponseEntity.ok(user);
    }
}
//{
//        "name": "John",
//        "age": 25,
//        "email": "john@example.com"
//        }