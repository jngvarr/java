package ru.jngvarr.webclient.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.jngvarr.webclient.fgn_clients.ServiceFGNClient;

@Controller
@RequiredArgsConstructor
public class SalonController {

    private final ServiceFGNClient serviceFGNClient;

    @GetMapping("/clients")
    public String showAll(Model model) {
        model.addAttribute("clients", serviceFGNClient.showAll());
        return "clients";
    }

    public String getClient(Model model, @RequestBody Client client) {

    }
}

