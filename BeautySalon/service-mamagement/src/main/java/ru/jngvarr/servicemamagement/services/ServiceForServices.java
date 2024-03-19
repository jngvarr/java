package ru.jngvarr.servicemamagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jngvarr.servicemamagement.repository.SalonServiceRepository;

@Service
@RequiredArgsConstructor
public class ServiceForServices {
    private final SalonServiceRepository salonServiceRepository;



}
