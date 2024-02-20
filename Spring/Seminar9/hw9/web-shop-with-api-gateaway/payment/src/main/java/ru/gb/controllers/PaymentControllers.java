package ru.gb.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentControllers {

    @GetMapping
    public String paymentMethod(){
        return "Сервис в разработке!!!" +
                "Скоро я запилю нормальный рабочий Payment-сервис!";
    }
}
