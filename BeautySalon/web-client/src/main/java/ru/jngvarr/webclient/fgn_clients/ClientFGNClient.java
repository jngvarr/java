package ru.jngvarr.webclient.fgn_clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.webclient.model.Client;

import java.util.List;

@FeignClient(name = "clients", url = "localhost:8081/clients")
public interface ClientFGNClient {
    @GetMapping
    public List<Client> getClients();

    @GetMapping("{id}")
    public Client getClient(@PathVariable Long id);

    @GetMapping("/by-contact/")
    public Client getClientByContact(String contact);

    @PostMapping
    public Client addClient(Client clientToAdd);

    @PutMapping("{id}")
    public Client update(@RequestBody Client newData, @PathVariable Long id);

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id);

    @GetMapping("/clear")
    public void clearAllData();

}
