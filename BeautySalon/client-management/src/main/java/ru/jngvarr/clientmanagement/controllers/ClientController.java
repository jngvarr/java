package ru.jngvarr.clientmanagement.controllers;

import dao.entities.people.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.clientmanagement.services.ClientService;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
@CrossOrigin(origins = "http://localhost:4200")
public class ClientController {
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
    public List<Client> getClientByPhone(@PathVariable String phoneNumber) {
        log.debug("number={}", phoneNumber);

        return clientService.getClientByContact(phoneNumber);
    }

    @GetMapping("/by-name")
    public List<Client> getClientsByName(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "lastName", required = false) String lastName) {
        log.debug("name={}, lastname={}", name, lastName);
        return (name.isEmpty() || lastName.isEmpty()) ?
                (name.isEmpty() ? clientService.getClientByLastName(lastName) : clientService.getClientByName(name)) :
                clientService.getClientByFullName(name, lastName);
    }
//    @GetMapping("/by-name/{name}/{lastName}")
//    public List<Client> getClientsByName(@PathVariable("name") String name, @PathVariable("lastName") String lastName) {
//        log.debug("name={}, lastname={}", name, lastName);
//        return clientService.getClientByName(name, lastName);
//    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public Client createClient(@RequestBody Client newClient) {
        log.debug("create-client {}", newClient);
        return clientService.addClient(newClient);
    }

    @PutMapping("/update/{id}")
    public Client updateClient(@RequestBody Client newClient, @PathVariable Long id) {
        return clientService.update(newClient, id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }
}
