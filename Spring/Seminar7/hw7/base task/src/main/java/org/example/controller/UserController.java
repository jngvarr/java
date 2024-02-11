package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {

    @GetMapping("/public-data")
    public String publicPage() {
        return "publicPage";
    }

    @GetMapping("/private-data")
    public String privatePage() {
        return "privatePage";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


}