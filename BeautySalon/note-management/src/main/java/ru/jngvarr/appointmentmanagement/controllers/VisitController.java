package ru.jngvarr.appointmentmanagement.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import dao.Visit;
import ru.jngvarr.appointmentmanagement.model.VisitData;
import ru.jngvarr.appointmentmanagement.services.VisitService;

import java.util.List;
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class VisitController {

    private final VisitService visitService;

    @GetMapping
    public List<Visit> getVisits() {
        log.debug("getVisits");
        return visitService.getVisits();
    }

    @GetMapping("/{id}")
    Visit getVisit(@PathVariable Long id) {
        return visitService.getVisit(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public Visit create(@RequestBody Visit visit){
        return visitService.create(visit);
    }

    @PutMapping("/update/{id}")
    public VisitData update(@RequestBody Visit visit, @PathVariable Long id){
        return visitService.update(visit, id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        visitService.delete(id);
    }
}
