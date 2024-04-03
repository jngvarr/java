package ru.jngvarr.appointmentmanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jngvarr.appointmentmanagement.feign_clients.ClientFeignClient;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientFeignClient clientFeignClient;

    
}
