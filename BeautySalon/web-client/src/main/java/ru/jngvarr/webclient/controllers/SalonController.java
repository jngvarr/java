package ru.jngvarr.webclient.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.webclient.model.Client;
import ru.jngvarr.webclient.services.SalonService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clients")
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    public String showAll(Model model) {
        System.out.println("show all");
        System.out.println(getClass().getSimpleName());
        model.addAttribute("clients", salonService.getAll());
        return "clients";
    }

    @GetMapping("/{id}")
    public String getClient(Model model, @PathVariable Long id) {
        System.out.println(id);
        model.addAttribute("client", salonService.getClient(id));
        return "client";
    }

    @GetMapping("/by-contact/{contact}")
    public String getClientByContact(Model model, @PathVariable String contact) {
        System.out.println(contact);
        model.addAttribute("client", salonService.getClientByContact(contact));
        return "client";
    }

    @PostMapping("/client-create")
    public String addClient(Model model, @RequestBody Client client) {
        System.out.println("create salonController");
        model.addAttribute("clients", salonService.addClient(client));
        return "redirect:/clients";
    }
//    @PostMapping("/client-create")
//    public Client addClient(@RequestBody Client client) {
//        System.out.println("create salonController");
//
//        return salonService.addClient(client);
//    }


    @GetMapping("/clear")
    public String getClient(Model model) {
        salonService.clear();
        model.addAttribute("clients", salonService.getAll());
        return "clients";
    }

    @PutMapping("/client-update/{id}")
    public String update(Model model, @RequestBody Client newData, @PathVariable long id) {
        salonService.update(newData, id);
        System.out.println("put update salonController");
        model.addAttribute("clients", salonService.getAll());
        return "redirect:/clients";
    }

    @DeleteMapping("/client-delete/{id}")
    public String delete(Model model, @PathVariable Long id) {
        System.out.println("delete salonController");
        salonService.delete(id);
        model.addAttribute("clients", salonService.getAll());
        return "redirect:/clients";
    }
}

