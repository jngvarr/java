package ru.jngvarr.webclient.fgn_clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.webclient.auth.ClientConfiguration;
import ru.jngvarr.webclient.model.Client;

import java.util.List;

@FeignClient(name = "clients", url = "http://localhost:8081", configuration = ClientConfiguration.class)
public interface ClientFGNClient {
    //    @RequestMapping(method = RequestMethod.GET, value = "/clients")
    @GetMapping("/clients")
    public List<Client> getClients();
//    public List<Client> getClients(@RequestHeader("Authorization") String authorizationHeader);

    @GetMapping("clients/{id}")
    public Client getClient(@PathVariable Long id);

    @GetMapping("clients/by-contact/{contact}")
    public Client getClientByContact(@PathVariable String contact);

    @PostMapping("/clients/create-client")
    public Client addClient(@RequestBody Client clientToAdd);

    @PutMapping("/clients/client-update/{id}")
    public Client update(@RequestBody Client newData, @PathVariable Long id);

    @DeleteMapping("/clients/client-delete/{id}")
    public void deleteClient(@PathVariable Long id);

    @GetMapping("/clients/clear")
    public void clearAllData();

}
