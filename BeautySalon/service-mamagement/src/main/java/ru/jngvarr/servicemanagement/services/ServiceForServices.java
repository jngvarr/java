package ru.jngvarr.servicemanagement.services;

import dao.Service;
import lombok.RequiredArgsConstructor;
import ru.jngvarr.servicemanagement.repositories.SalonServiceRepository;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceForServices {
    private final SalonServiceRepository salonServiceRepository;

    public List<Service> getServices(){
        return salonServiceRepository.findAll();
    }



}
