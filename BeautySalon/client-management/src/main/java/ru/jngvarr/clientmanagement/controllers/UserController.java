package ru.jngvarr.clientmanagement.controllers;

import dao.entities.people.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.clientmanagement.repositories.UserRepository;
import ru.jngvarr.clientmanagement.services.UserDetailsServiceImpl;
import ru.jngvarr.clientmanagement.services.UserService;

@Data
@Log4j2
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/byEmail/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/registration")
    public User userRegistration(@RequestBody User user) {
        log.debug("create{} ", user.getId());
        return userService.createUser(user);
    }
}
