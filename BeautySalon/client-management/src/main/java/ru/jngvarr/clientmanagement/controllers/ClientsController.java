package ru.jngvarr.clientmanagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.clientmanagement.model.Client;
import ru.jngvarr.clientmanagement.services.ClientService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientsController {
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<Client>> showAll() {
        return ResponseEntity.ok().body(clientService.showAll());
    }

    @GetMapping("/clear")
    public ResponseEntity<Void> clearAllData() {
        clientService.clearAllData();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        return new ResponseEntity<>(clientService.getClient(id), HttpStatus.OK);
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<Client> getClientByPhone(@PathVariable String phoneNumber) {
        return new ResponseEntity<>(clientService.getClientByPhone(phoneNumber), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client newClient) {
        return new ResponseEntity<>(clientService.addClient(newClient), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@RequestBody Client newClient, @PathVariable Long id) {
        return new ResponseEntity<>(clientService.update(newClient, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
