package ru.jngvarr.webclient.feign_clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import dao.people.Client;
import ru.jngvarr.webclient.auth.FeignClientConfiguration;

import java.util.List;

@FeignClient(name = "clients", configuration = FeignClientConfiguration.class)
public interface ClientFeignClient {
    //    @RequestMapping(method = RequestMethod.GET, value = "/clients")
    @GetMapping("/visits")
    List<Client> getClients();
//    public List<Client> getClients(@RequestHeader("Authorization") String authorizationHeader);

    @GetMapping("/visits/{id}")
    Client getClient(@PathVariable Long id);

    @GetMapping("/visits/by-contact/{contact}")
    Client getClientByContact(@PathVariable String contact);

    @RequestMapping(value = "/visits/create-client", method = RequestMethod.POST)
    Client addClient(@RequestBody Client clientToAdd);

    @RequestMapping(value = "/visits/client-update/{id}", method = RequestMethod.PUT)
    Client update(@RequestBody Client newData, @PathVariable Long id);

    @RequestMapping(value = "/visits/client-delete/{id}", method = RequestMethod.DELETE)
    void deleteClient(@PathVariable Long id);

    @GetMapping("/visits/clear")
    void clearAllData();

}
