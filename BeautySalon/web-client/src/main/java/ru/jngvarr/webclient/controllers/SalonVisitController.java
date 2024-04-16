package ru.jngvarr.webclient.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.jngvarr.webclient.services.SalonService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/visits")
public class SalonVisitController {
    private final SalonService salonService;

    @GetMapping
    public String getVisits(Model model) {
        model.addAttribute(salonService.getVisits());
        return "visits";
    }
}
