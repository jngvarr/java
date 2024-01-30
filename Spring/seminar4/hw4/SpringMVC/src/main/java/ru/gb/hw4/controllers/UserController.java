package ru.gb.hw4.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.gb.hw4.model.User;
import ru.gb.hw4.services.UserService;

/**
 * Контроллер, отвечающий за обработку запросов, связанных с пользователями.
 */
@Controller
public class UserController {
    private UserService userService;
    /**
     * Конструктор контроллера с инъекцией зависимости от UserService.
     *
     * @param userService Сервис для работы с пользователями.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Обработка запроса на отображение страницы с данными о пользователях.
     *
     * @param model Модель для передачи данных в представление.
     * @return Возвращаем представление "task3" с данными о пользователях.
     */
    @GetMapping("/task3")
    public String viewUsers(Model model) {
        model.addAttribute("users", userService.getUsers());
        return "task3";
    }

    /**
     * Обработка запроса на добавление нового пользователя и перенаправление на страницу успешной регистрации.
     *
     * @param user       Новый пользователь, переданный из формы.
     * @param attributes Атрибуты перенаправления для передачи данных между запросами.
     * @return перенаправление на страницу успешной регистрации.
     * @throws InterruptedException Исключение, если возникли проблемы с потоком выполнения.
     */
    @PostMapping("/task3")
    public String addUser(User user, RedirectAttributes attributes) throws InterruptedException {
        userService.addUser(user);
        attributes.addFlashAttribute("name", user.getName());
        attributes.addFlashAttribute("email", user.getEmail());
        return "redirect:/successRegistration";
    }

    /**
     * Обработка запроса на отображение страницы успешной регистрации.
     *
     * @param name  Имя пользователя.
     * @param email Электронный адрес пользователя.
     * @param model Модель для передачи данных в представление.
     * @return Возвращаем представление "successRegistration" с данными успешной регистрации.
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
