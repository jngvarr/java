package ru.gb.hw4.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.gb.hw4.model.User;
import ru.gb.hw4.services.UserService;

@Controller
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/task3")
    public String viewUsers(Model model) {
        model.addAttribute("users", userService.getUsers());
        return "task3";
    }

    @PostMapping("/task3")
    public String addUser(User user, RedirectAttributes attributes) throws InterruptedException {
        userService.addUser(user);
        attributes.addFlashAttribute("name", user.getName());
        attributes.addFlashAttribute("email", user.getEmail());
        return "redirect:/successRegistration";
    }

    /**
     * Отображение HTML-страницы регистрации пользователя
     * Обработка HTTP-запросов, направленных на путь "/task1"
     *
     * @return возвращает логическое имя шаблона
     */
    @GetMapping("/successRegistration")
    public String showSuccessRegistration(@ModelAttribute("name") String name,
                                          @ModelAttribute("email") String email,
                                          Model model) {
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        return "successRegistration";
    }
}
