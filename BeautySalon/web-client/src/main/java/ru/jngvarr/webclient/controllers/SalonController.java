package ru.jngvarr.webclient.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.jngvarr.webclient.services.SalonService;

@Controller
@RequiredArgsConstructor
public class SalonController {

    private final SalonService salonService;

    @GetMapping("/clients")
    public String showAll(Model model) {
        model.addAttribute("clients", salonService.getAll());
        return "clients";
    }

//    public String getClient(Model model, @RequestBody Client client) {
//
//        return "client";
//    }
}

