package ru.gb.controllers;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {
    private Counter myRequestCounter;

    @GetMapping
    public String showAll() {
        myRequestCounter.increment();
        return "Service is developing" +
                " Soon I will create a normal working storage-service!";
    }
}
