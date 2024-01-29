package ru.gb.hw4.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

/**
 * Обработка HTML запросов домашнего задания четвертого семинара
 */
@Controller
public class Sem4hwController {
    /**
     * Отображение HTML-страницы домашнего задания четвертого семинара
     * Обработка HTTP-запросов, направленных на путь "/home"
     *
     * @return возвращает логическое имя шаблона
     */
    @RequestMapping("/home")
    public String task() {
        return "home";
    }

    /**
     * Отображение HTML-страницы первого домашнего задания четвертого семинара
     * Обработка HTTP-запросов, направленных на путь "/task1"
     *
     * @return возвращает логическое имя шаблона
     */
    @GetMapping("/task1")
    public String helloWorld() {
        return "task1";
    }

    /**
     * Отображение HTML-страницы второго домашнего задания четвертого семинара
     * Обработка HTTP-запросов, направленных на путь "/task2"
     *
     * @return возвращает логическое имя шаблона
     */
    @GetMapping("/task2")
    public String whatIsTheDate(Model model) {
        model.addAttribute("date", LocalDate.now().toString());
        return "task2";
    }
}
