package ru.jngvarr.storagemanagement.controllers;

import dao.Consumable;
import exceptions.NeededObjectNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.storagemanagement.service.StorageService;

import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/storage")
public class StorageController {
    private final StorageService storageService;

    @GetMapping
    public List<Consumable> getConsumables(){
        log.debug("get");
        return storageService.getConsumables();
    }

    @GetMapping("/{id}")
    public Consumable getConsumable(@PathVariable Long id){
        return storageService.getConsumable(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public Consumable create(@RequestBody Consumable consumable){
        return storageService.add(consumable);
    }

    @PutMapping("/update/{id}")
    public Consumable update(@RequestBody Consumable newData,@PathVariable Long id){
        return storageService.update(newData, id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        storageService.delete(id);
    }
}
