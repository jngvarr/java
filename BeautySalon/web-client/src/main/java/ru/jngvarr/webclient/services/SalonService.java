package ru.jngvarr.webclient.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.jngvarr.webclient.fgn_clients.ClientFeignClient;

import java.util.List;
import dao.people.Client;

@Log4j2
@Service
@RequiredArgsConstructor
public class SalonService {
    private final ClientFeignClient clientFGNClient;

    public List<Client> getAll() {
        return clientFGNClient.getClients();
    }

    public Client getClient(Long id) {
        return clientFGNClient.getClient(id);
    }

    public Client addClient(Client client) {
        log.debug("create {}", client);
        return clientFGNClient.addClient(client);
    }

    public Client getClientByContact(String contact) {
        return clientFGNClient.getClientByContact(contact);
    }

    public Client update(Client newData, Long id) {
        return clientFGNClient.update(newData, id);
    }

    public void delete(Long id) {
        clientFGNClient.deleteClient(id);
    }

    public void clear() {
        clientFGNClient.clearAllData();
    }
}
