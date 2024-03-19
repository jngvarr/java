package ru.jngvarr.webclient.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.webclient.model.Client;
import ru.jngvarr.webclient.services.SalonService;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/clients")
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    public String showAll(Model model) {
        log.debug("show all");
        model.addAttribute("clients", salonService.getAll());
        return "clients";
    }

    @GetMapping("/{id}")
    public String getClient(Model model, @PathVariable Long id) {
        log.debug(id);
        model.addAttribute("client", salonService.getClient(id));
        return "client";
    }

    @GetMapping("/by-contact/{contact}")
    public String getClientByContact(Model model, @PathVariable String contact) {
        log.debug(contact);
        model.addAttribute("client", salonService.getClientByContact(contact));
        return "client";
    }

//    @PostMapping("/client-create")
//    public Client addClient(@RequestBody Client client) {
//       log.debug("create salonController");
//
//        return salonService.addClient(client);
//    }

    @GetMapping("/clear")
    public String getClient(Model model) {
        salonService.clear();
        model.addAttribute("clients", salonService.getAll());
        return "clients";
    }

    @GetMapping("/client-create")
    public String toCreateClient(Model model) {
        model.addAttribute("client", new Client());
        return "client-create";
    }

    @PostMapping("/client-create")
    public String addClient(Model model, Client client) {
        log.debug("create {}", client);
        salonService.addClient(client);
        model.addAttribute("clients", salonService.getAll());
        return "clients";
    }

    @GetMapping("/client-update/{id}")
    public String updateClientForm(Model model, @PathVariable long id) {
        Client oldClient = salonService.getClient(id);
        model.addAttribute("client", oldClient);
        return "client-update";
    }

    @GetMapping ("/client-update")
    public String update(@ModelAttribute("client") Client client) {
        System.out.println(client);
        log.debug("put {}", client);
        salonService.update(client, client.getId());
        return "clients";
    }

    @DeleteMapping("/client-delete/{id}")
    public String delete(@PathVariable Long id) {
        log.debug("delete {}", id);
        salonService.delete(id);
        return "redirect:/clients";
    }
}

