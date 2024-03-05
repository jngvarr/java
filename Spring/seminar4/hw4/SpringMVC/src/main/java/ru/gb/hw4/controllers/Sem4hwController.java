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
@RequestMapping
public class Sem4hwController {
//    /**
//     * Обработка HTTP-запросов, направленных на путь "/home"
//     * Отображение HTML-страницы домашнего задания четвертого семинара
//     *
//     * @return возвращает логическое имя шаблона
//     */
//    @GetMapping()
//    public String task() {
//        return "home";
//    }

    /**
     * Обработка HTTP-запросов, направленных на путь "/task1"
     * Отображение HTML-страницы первого задания четвертого семинара
     *
     * @return возвращает логическое имя шаблона
     */
    @GetMapping("/task1")
    public String helloWorld() {
        return "task1";
    }

    /**
     * Обработка HTTP-запросов, направленных на путь "/task2"
     * Отображение HTML-страницы второго задания четвертого семинара
     *
     * @return возвращает логическое имя шаблона
     */
    @GetMapping("/task2")
    public String whatIsTheDate(Model model) {
        model.addAttribute("date", LocalDate.now().toString());
        return "task2";
    }
}
