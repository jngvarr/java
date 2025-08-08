package jngvarr.ru.pto_ackye_rzhd.services;

import jngvarr.ru.pto_ackye_rzhd.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.entities.Substation;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.mappers.MeterMapper;
import jngvarr.ru.pto_ackye_rzhd.repositories.others.SubstationRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class SubstationService {
    private final SubstationRepository repository;

    public Substation getSubstationById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NeededObjectNotFound("Substation not found: " + id));
    }

    public Substation create(Substation substation) {
        return repository.save(substation);
    }

    public Optional<Substation> findByName(String substationName, String stationName) {
        return repository.findByNameAndAndStationName(substationName, stationName);
    }
}
