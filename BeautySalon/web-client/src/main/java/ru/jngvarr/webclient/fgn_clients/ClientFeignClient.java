package ru.jngvarr.webclient.fgn_clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.webclient.auth.FeignClientConfiguration;
import dao.people.Client;
import java.util.List;

@FeignClient(name = "clients", configuration = FeignClientConfiguration.class)
public interface ClientFeignClient {
    //    @RequestMapping(method = RequestMethod.GET, value = "/clients")
    @GetMapping("/clients")
    List<Client> getClients();
//    public List<Client> getClients(@RequestHeader("Authorization") String authorizationHeader);

    @GetMapping("/clients/{id}")
    Client getClient(@PathVariable Long id);

    @GetMapping("/clients/by-contact/{contact}")
    Client getClientByContact(@PathVariable String contact);

    @RequestMapping(value = "/clients/create-client", method = RequestMethod.POST)
    Client addClient(@RequestBody Client clientToAdd);

    @RequestMapping(value = "/clients/client-update/{id}", method = RequestMethod.PUT)
    Client update(@RequestBody Client newData, @PathVariable Long id);

    @RequestMapping(value = "/clients/client-delete/{id}", method = RequestMethod.DELETE)
    void deleteClient(@PathVariable Long id);

    @GetMapping("/clients/clear")
    void clearAllData();

}
