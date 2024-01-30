package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Контроллер обработки запросов БД пользователей
 */
@Controller
@AllArgsConstructor
@Log
public class UserController {
    private final UserService userService;
    /**
     * Обработка запроса на получение списка пользователей.
     *
     * @param model Модель для передачи данных в представление.
     * @return Возвращаем представление "user-list".
     */
    @GetMapping("/users")
    public String findAll(Model model) {
        List<User> users = userService.findAll();

        model.addAttribute("users", users);
        return "user-list";
        //return "home.html";
    }
    /**
     * Обработка запроса на отображение формы создания пользователя.
     *
     * @param model Модель для передачи данных в представление.
     * @return Возвращаем представление "user-create" с новым пользователем в модели.
     */
    @GetMapping("/user-create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user-create";
    }
    /**
     * Обработка запроса на создание нового пользователя.
     *
     * @param user Новый пользователь, переданный из формы.
     * @return После создания пользователя перенаправляем на страницу списка пользователей.
     */
    @PostMapping("/user-create")
    public String createUser(User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    /**
     * Обработка запроса на удаление пользователя.
     *
     * @param id Идентификатор пользователя.
     * @return Перенаправление на страницу списка пользователей.
     */
    @GetMapping("user-delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
    /**
     * Обработка запроса на удаление пользователя.
     *
     * @param id Идентификатор пользователя.
     * @return Перенаправление на страницу списка пользователей.
     */
    @GetMapping("/user-update/{id}")
    public String updateUserForm(@PathVariable("id") int id, Model model) {
        User existingUser = userService.getOne(id);
        model.addAttribute("user", existingUser);
        return "user-update";
    }
    /**
     * Обработка запроса на обновление данных пользователя.
     *
     * @param user Данные пользователя, переданные из формы.
     * @return Перенаправление на страницу списка пользователей.
     */
    @PostMapping("/user-update")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.updateUser(user.getId(), user);
        return "redirect:/users";
    }
}
