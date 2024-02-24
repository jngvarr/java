package ru.gb.controllers;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
public class StorageController {

    private final Counter requestCounter = Metrics.counter("hw11task_request_counter");

    @GetMapping
    public String showAll(){
        requestCounter.increment();
        return "Service is developing" +
                " Soon I will create a normal working storage-service!";
    }
}
