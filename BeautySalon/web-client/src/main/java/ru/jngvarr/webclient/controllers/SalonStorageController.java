package ru.jngvarr.webclient.controllers;

import dao.entities.Consumable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.webclient.services.SalonService;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/storage")
public class SalonStorageController {
    private final SalonService salonService;

    @GetMapping
    public String getConsumables(Model model) {
        model.addAttribute("consumables", salonService.getConsumables());
        return "consumables";
    }

    @GetMapping("/{id}")
    public String getConsumable(Long id, Model model) {
        model.addAttribute(salonService.getConsumable(id));
        return "consumable";
    }

    @GetMapping("/create-view")
    public String toCreateConsumables(Model model) {
        model.addAttribute("consumable", new Consumable());
        return "consumable-create";
    }

    @PostMapping("/create-action")
    public String addConsumable(Model model, Consumable consumable) {
        log.debug("add cons action {}", consumable);
        salonService.addConsumable(consumable);
        model.addAttribute("consumables", salonService.getConsumables());
        return "consumables";
    }

    @GetMapping("/update-view/{id}")
    public String toUpdateConsumable(Model model, @PathVariable Long id) {
        model.addAttribute("consumable", salonService.getConsumable(id));
        return "consumable-update";
    }

    @PostMapping("/update-action")
    public String updateConsumable(@ModelAttribute("consumable") Consumable newData) {
        salonService.updateConsumable(newData, newData.getId());
        return "redirect:/consumables";
    }

    @GetMapping("/delete-action/{id}")
    public String deleteConsumable(@PathVariable Long id) {
        salonService.deleteConsumable(id);
        return "redirect:/consumables";

    }
}