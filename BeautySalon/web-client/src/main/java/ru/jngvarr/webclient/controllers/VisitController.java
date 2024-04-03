package ru.jngvarr.webclient.controllers;

import dao.Visit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/visits")
public class VisitController {

    @GetMapping()
    public List<Visit>getVisits(){
        return
    }
}
