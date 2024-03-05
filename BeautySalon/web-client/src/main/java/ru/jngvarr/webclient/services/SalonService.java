package ru.jngvarr.webclient.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jngvarr.webclient.fgn_clients.ClientFGNClient;

@Service
@RequiredArgsConstructor
public class SalonService {
    private final ClientFGNClient clientFGNClient;
}
