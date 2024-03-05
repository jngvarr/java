package ru.jngvarr.clientmanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jngvarr.clientmanagement.model.Client;

import ru.jngvarr.clientmanagement.repositories.ClientsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientsRepository clientsRepository;

    public List<Client> showAll() {
        return clientsRepository.findAll();
    }

    public Client getClient(Client client) {
        return clientsRepository.getReferenceById(client.getId());
    }

    public Client addClient(Client client){
        return clientsRepository.save(client);
    }

    public void deleteClient(Client client){
        clientsRepository.deleteById(client.getId());
    }
}

