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

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        return new ResponseEntity<>(clientService.getClient(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client newClient) {
        return new ResponseEntity<>(clientService.addClient(newClient), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Client> updateClient(Client newClient) {
        return new ResponseEntity<>(clientService.addClient(newClient), HttpStatus.OK);
    }
    @DeleteMapping
    public ResponseEntity<Client> deleteClient(Client newClient) {
        return new ResponseEntity<>(clientService.addClient(newClient), HttpStatus.NO_CONTENT);
    }
}
