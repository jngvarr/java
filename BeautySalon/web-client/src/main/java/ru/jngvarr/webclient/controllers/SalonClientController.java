package ru.jngvarr.webclient.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.webclient.services.SalonService;
import dao.entities.people.Client;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/clients")
public class SalonClientController {

    private final SalonService salonService;

    @GetMapping
    public String showAll(Model model) {
        model.addAttribute("clients", salonService.getClients());
        return "clients";
    }

    @GetMapping("/{id}")
    public String getClient(Model model, @PathVariable Long id) {
        model.addAttribute("client", salonService.getClient(id));
        return "client";
    }

    @GetMapping("/by-contact/{contact}")
    public String getClientByContact(Model model, @PathVariable String contact) {
        model.addAttribute("client", salonService.getClientByContact(contact));
        return "client";
    }

    @GetMapping("/clear")
    public String getClient(Model model) {
        salonService.clear();
        model.addAttribute("clients", salonService.getClients());
        return "clients";
    }

    @GetMapping("/create-view")
    public String toCreateClient(Model model) {
        log.debug("create-view");
        model.addAttribute("client", new Client());
        return "client-create";
    }

    @PostMapping("/create-action")
    public String addClient(Model model, Client client) {
        salonService.addClient(client);
        model.addAttribute("clients", salonService.getClients());
        return "clients";
    }

    @GetMapping("/update-view/{id}")
    public String updateClientForm(Model model, @PathVariable long id) {
        Client oldClient = salonService.getClient(id);
        model.addAttribute("client", oldClient);
        return "client-update";
    }

    @PostMapping ("/update-action")
    public String update(@ModelAttribute("client") Client client) {
        log.debug("put {}", client);
        salonService.update(client, client.getId());
        return "redirect:/clients";
    }

    @GetMapping("/delete-action/{id}")
    public String delete(@PathVariable Long id) {
        log.debug("delete {}", id);
        salonService.deleteClient(id);
        return "redirect:/clients";
    }
}

