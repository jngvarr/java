package ru.gb.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
public class StorageController {
    @GetMapping
    public String storageMethod(){
        return "Service is developing" +
                " Soon I will create a normal working storage-service!";
    }
}
