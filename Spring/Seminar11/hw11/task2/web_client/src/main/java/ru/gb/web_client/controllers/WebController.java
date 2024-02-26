package ru.gb.web_client.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.web_client.services.ShopService;

@Controller
@AllArgsConstructor
public class WebController {

    /**
     * Сервис магазина.
     */
    private final ShopService shopService;

    /**
     * Домашняя страница.
     * @param model модель для передачи данных представлению.
     * @return домашнюю страницу.
     */
    @GetMapping("/")
    public String homePage(Model model,
                           @RequestParam(value = "confirm", required = false) String confirm){
        model.addAttribute("products", shopService.getAll());
        if (confirm != null){
            model.addAttribute("confirm", confirm);
        }
        return "home";
    }
}
