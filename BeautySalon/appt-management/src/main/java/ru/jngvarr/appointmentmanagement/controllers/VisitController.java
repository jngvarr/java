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

import java.time.LocalDate;
import java.util.Date;
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

    @GetMapping("/by-date/{date}")
    public List<Visit> getVisitsByDate(@PathVariable LocalDate date) {
        return visitService.getVisitsByDate(date);
    }

    @GetMapping("/by-client/{client}")
    public List<Visit> getVisitsByClient(@PathVariable String client) {
        return visitService.getVisitsByClient(client);
    }

    @GetMapping("/by-master/{masterId}")
    public List<Visit> getVisitsByMaster(@PathVariable Long masterId) {
        return visitService.getVisitsByMaster(masterId);
    }

    @GetMapping("/by-service/{serviceId}")
    public List<Visit> getVisitsByService(@PathVariable Long serviceId) {
        return visitService.getVisitsByService(serviceId);
    }

//    @GetMapping("/clients")
//    public List<Client> getClients(){
//        log.debug("getClients-VisitController");
//        return visitService.getClients();
//    }
//
//    @GetMapping("/services")
//    public List<Servize> getServices(){
//        return visitService.getServices();
//    }
//
//    @GetMapping("/staff")
//    public List<Employee> getEmployees(){
//        return visitService.getEmployees();
//    }


    @GetMapping("/{id}")
    Visit getVisit(@PathVariable Long id) {
        return visitService.getVisit(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public Visit create(@RequestBody Visit visit) {
        log.debug("create Visit {}", visit);
        return visitService.create(visit);
    }

    @PutMapping("/update/{id}")
    public Visit update(@RequestBody Visit visit, @PathVariable Long id) {
        log.debug("update {}", visit);
        Visit update = visitService.update(visit, id);
        return update;
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        visitService.delete(id);
    }
}
