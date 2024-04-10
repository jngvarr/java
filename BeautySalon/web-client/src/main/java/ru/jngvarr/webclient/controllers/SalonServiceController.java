package ru.jngvarr.webclient.controllers;

import dao.Servize;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.webclient.services.SalonService;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/services")
public class SalonServiceController {

    private final SalonService salonService;

    @GetMapping
    public String showAll(Model model) {
        model.addAttribute("services", salonService.getServices());
        return "services";
    }

    @GetMapping("/{id}")
    public String getEmployee(Model model, @PathVariable Long id) {
        model.addAttribute("employee", salonService.getEmployee(id));
        return "service";
    }

    @GetMapping("/create-view")
    public String toCreateService(Model model) {
        model.addAttribute("consumables", salonService.getConsumables() );
        model.addAttribute("service", new Servize());
        return "service-create";
    }

    @PostMapping("/create-action")
    public String addService(Model model, Servize service) {
        salonService.addService(service);
        model.addAttribute("services", salonService.getServices());
        return "services";
    }

    @GetMapping("/update-view/{id}")
    public String toUpdateService(Model model, @PathVariable long id) {
        Servize oldService = salonService.getService(id);
        model.addAttribute("consumables", salonService.getConsumables());
        model.addAttribute("service", oldService);
        log.debug("update-view Service {}", oldService);
        return "service-update";
    }

    @PostMapping ("/update-action")
    public String updateService(@ModelAttribute("service") Servize newData) {
                salonService.updateService(newData, newData.getId());
        log.debug("update-action Service {}", newData);
        return "redirect:/services";
    }

    @GetMapping("/delete-action/{id}")
    public String delete(@PathVariable Long id) {
        salonService.deleteService(id);
        return "redirect:/services";
    }
}

