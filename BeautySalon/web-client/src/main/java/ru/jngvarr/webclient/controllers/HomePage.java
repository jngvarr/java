package ru.jngvarr.webclient.controllers;

import dao.entities.Visit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.jngvarr.webclient.services.SalonService;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Controller
public class HomePage {
    private final SalonService salonService;

    @GetMapping("/home")
    public String home(Model model) {
        log.debug("home");
        List<Visit> visits = salonService.getVisits();
        model.addAttribute("visits", visits);

        return "home";
    }
}
