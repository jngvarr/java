package ru.jngvarr.clientmanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jngvarr.clientmanagement.model.Client;
import ru.jngvarr.clientmanagement.repositories.ClientsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientsRepository clientsRepository;

    public List<Client> showAll() {
        return clientsRepository.findAll();
    }

    public Client getClient(Long id) {
        return clientsRepository.getReferenceById(id);
    }

    public Client addClient(Client client) {
        return clientsRepository.save(client);
    }

    public Client update(Client updateClient) {
//        Client newClient =
       return clientsRepository.save(updateClient);
    }

    public void deleteClient(Client client) {
        clientsRepository.deleteById(client.getId());
    }
}

