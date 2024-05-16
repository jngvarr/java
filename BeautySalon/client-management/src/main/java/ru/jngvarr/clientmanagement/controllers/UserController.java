package ru.jngvarr.clientmanagement.controllers;

import dao.entities.people.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.clientmanagement.services.UserDetailsServiceImpl;

@Data
@Log4j2
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserDetailsServiceImpl userService;

    @GetMapping("/byEmail/{email}")
    public UserDetails getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/byUserName/{username}")
    public UserDetails getUserByUserName(@PathVariable String username) {
        return userService.loadUserByUsername(username);
    }

    @PostMapping("/create")
    public UserDetails createUser(@RequestBody User user) {
        log.debug("create{} ", user.getId());
        return userService.addUser(user);
    }
}
