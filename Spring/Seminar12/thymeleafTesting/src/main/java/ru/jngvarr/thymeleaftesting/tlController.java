package ru.jngvarr.thymeleaftesting;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class tlController {

    //    @GetMapping("/hello")
//    @RequestMapping
//    public String home() {
//        return "index";
//    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
