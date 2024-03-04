package ru.jngvarr.beautysalon.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jngvarr.beautysalon.model.people.Client;
import ru.jngvarr.beautysalon.repositories.ClientsRepository;

import java.awt.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientsRepository clientsRepository;

    public List<Client> showAll() {
        return clientsRepository.findAll();
    }
}

