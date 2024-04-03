package ru.jngvarr.webclient.controllers;

import dao.Visit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.jngvarr.webclient.services.SalonService;

import java.util.List;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class VisitController {
    private final SalonService salonService;

    @GetMapping()
    public List<Visit> getVisits() {
        return salonService.getVisits();
    }
}