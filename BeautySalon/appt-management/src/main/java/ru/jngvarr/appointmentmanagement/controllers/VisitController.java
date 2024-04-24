package ru.jngvarr.appointmentmanagement.controllers;

import dao.entities.Servize;
import dao.entities.people.Client;
import dao.entities.people.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import dao.entities.Visit;
import ru.jngvarr.appointmentmanagement.model.VisitData;
import ru.jngvarr.appointmentmanagement.services.VisitService;

import java.util.List;
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/visits")
@CrossOrigin(origins = "http://localhost:4200")
public class VisitController {

    private final VisitService visitService;

    @GetMapping
    public List<Visit> getVisits() {
        log.debug("getVisits-VisitController");
        return visitService.getVisits();
    }
    @GetMapping("/clients")
    public List<Client> getClients(){
        log.debug("getClients-VisitController");
        return visitService.getClients();
    }

    @GetMapping("/services")
    public List<Servize> getServices(){
        return visitService.getServices();
    }

    @GetMapping("/staff")
    public List<Employee> getEmployees(){
        return visitService.getEmployees();
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
