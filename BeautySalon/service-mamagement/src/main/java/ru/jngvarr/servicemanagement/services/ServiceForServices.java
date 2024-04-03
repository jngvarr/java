package ru.jngvarr.servicemanagement.services;

import dao.Servize;
import lombok.*;
import org.springframework.stereotype.Service;
import ru.jngvarr.servicemanagement.repositories.SalonServiceRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceForServices {
    private final SalonServiceRepository salonServiceRepository;

    public List<Servize> getServices(){
        return salonServiceRepository.findAll();
    }



}
