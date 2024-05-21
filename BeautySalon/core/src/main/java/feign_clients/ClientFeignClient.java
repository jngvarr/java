package feign_clients;

import dao.entities.people.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "clients", configuration = JwtFeignConfig.class)
public interface ClientFeignClient {
    @GetMapping("/clients")
    List<Client> getClients();

    @GetMapping("/clients/{id}")
    Client getClient(@PathVariable Long id);

    @GetMapping("/clients/by-contact/{contact}")
    Client getClientByContact(@PathVariable String contact);

    @RequestMapping(value = "/clients/create", method = RequestMethod.POST)
    Client addClient(@RequestBody Client clientToAdd);

    @RequestMapping(value = "/clients/update/{id}", method = RequestMethod.PUT)
    Client updateClient(@RequestBody Client newData, @PathVariable Long id);

    @RequestMapping(value = "/clients/delete/{id}", method = RequestMethod.DELETE)
    void deleteClient(@PathVariable Long id);

    @GetMapping("/clients/clear")
    void clearAllData();

}
