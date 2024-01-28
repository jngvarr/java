package ru.gb.hw4.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
public class Sem4hwController {
    @RequestMapping("/task1")
    public String task() {
        return "task1";
    }

    @GetMapping("/home")
    public String helloWorld() {
        return "home";
    }
    @GetMapping("/task2")
    public String whatIsTheDate(Model model){
        model.addAttribute("date", LocalDate.now().toString());
        return "task2";
    }
}
