package ru.jngvarr.clientmanagement.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.clientmanagement.model.Client;
import ru.jngvarr.clientmanagement.services.ClientService;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientsController {
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<Client>> showAll() {
        return ResponseEntity.ok().body(clientService.getClients());
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

    @GetMapping("/by-contact/{phoneNumber}")
    public ResponseEntity<Client> getClientByPhone(@PathVariable String phoneNumber) {
        return new ResponseEntity<>(clientService.getClientByContact(phoneNumber), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create-client")
    public Client createClient(@RequestBody Client newClient) {
        return clientService.addClient(newClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@RequestBody Client newClient, @PathVariable Long id) {
        return new ResponseEntity<>(clientService.update(newClient, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }
}
