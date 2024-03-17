package ru.jngvarr.webclient.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jngvarr.webclient.fgn_clients.ClientFGNClient;
import ru.jngvarr.webclient.model.Client;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonService {
    private final ClientFGNClient clientFGNClient;

    public List<Client> getAll() {
        return clientFGNClient.getClients();
    }

    public Client getClient(Long id) {
        return clientFGNClient.getClient(id);
    }

    public Client addClient(Client client) {        System.out.println("create salonservice");

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
