package ru.jngvarr.webclient.services;

import dao.Visit;
import ru.jngvarr.webclient.feign_clients.ServiceFGNClient;
import ru.jngvarr.webclient.feign_clients.StaffFGNClient;
import ru.jngvarr.webclient.feign_clients.VisitFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.jngvarr.webclient.feign_clients.ClientFeignClient;

import java.util.List;

import dao.people.Client;

@Log4j2
@Service
@RequiredArgsConstructor
public class SalonService {
    private final ClientFeignClient clientFeignClient;
    private final VisitFeignClient visitFeignClient;
    private final ServiceFGNClient serviceFeignClient;
    private final StaffFGNClient staffFeignClient;


    public List<Client> getAll() {
        return clientFeignClient.getClients();
    }

    public List<Visit> getVisits() {
        return visitFeignClient.getVisits();
    }

    public Client getClient(Long id) {
        return clientFeignClient.getClient(id);
    }

    public Client addClient(Client client) {
        log.debug("create {}", client);
        return clientFeignClient.addClient(client);
    }

    public Client getClientByContact(String contact) {
        return clientFeignClient.getClientByContact(contact);
    }

    public Client update(Client newData, Long id) {
        return clientFeignClient.update(newData, id);
    }

    public void delete(Long id) {
        clientFeignClient.deleteClient(id);
    }

    public void clear() {
        clientFeignClient.clearAllData();
    }
}
