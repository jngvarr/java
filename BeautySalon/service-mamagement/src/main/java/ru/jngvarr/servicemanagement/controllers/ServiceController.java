package ru.jngvarr.servicemanagement.controllers;

import dao.Servize;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.servicemanagement.services.ServiceForServices;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/services")
public class ServiceController {
    private final ServiceForServices service;

    @GetMapping
    public List<Servize> getServices() {
        return service.getServices();
    }

    @GetMapping("/{id}")
    public Servize getService(@PathVariable Long id) {
        return service.getService(id);
    }

    @GetMapping("/duration/{id}")
    public int getServiceDuration(@PathVariable Long id) {
        return service.getServiceDuration(id);
    }

    @PostMapping
    public Servize addService(@RequestBody Servize newService) {
        return service.addService(newService);
    }

    @PutMapping("/{id}")
    public Servize update(@RequestBody Servize newData, @PathVariable Long id) {
        return service.update(newData, id);
    }

    @DeleteMapping
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


}
