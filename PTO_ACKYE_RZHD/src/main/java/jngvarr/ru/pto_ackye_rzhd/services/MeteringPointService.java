package jngvarr.ru.pto_ackye_rzhd.services;

import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughDataException;
import jngvarr.ru.pto_ackye_rzhd.repositories.MeteringPointRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class MeteringPointService {
    private final MeteringPointRepository meteringPointRepository;

    public List<MeteringPoint> getAll() {
        return meteringPointRepository.findAll();
    }

    public MeteringPoint getIik(Long id) {
        Optional<MeteringPoint> neededMeteringPoint = meteringPointRepository.findById(id);
        if (neededMeteringPoint.isPresent()) {
            return neededMeteringPoint.get();
        } else throw new NeededObjectNotFound("Employee not found!");
    }

    public MeteringPoint create(MeteringPoint iik) {
        if (iik.getName() != null
                && iik.getMeteringPointAddress() != null
                && iik.getSubstation() != null) {
            return meteringPointRepository.save(iik);
        } else {
            throw new NotEnoughDataException("Not enough data to create MeteringPoint");
        }
    }

    public List<MeteringPoint> createIiks(List<MeteringPoint> iiks) {
        for (MeteringPoint iik : iiks) {
            if (iik.getName() == null || iik.getMeteringPointAddress() == null || iik.getSubstation() == null) {
                throw new NotEnoughDataException("Not enough data to create one or more MeteringPoints");
            }
        }
        return meteringPointRepository.saveAll(iiks);
    }

    public MeteringPoint update(MeteringPoint newData, Long id) {
        MeteringPoint existingMeteringPoint = getIik(id);
        existingMeteringPoint.setName(newData.getName());
        existingMeteringPoint.setMeteringPointAddress(newData.getMeteringPointAddress());
        existingMeteringPoint.setSubstation(newData.getSubstation());
        return meteringPointRepository.save(existingMeteringPoint);
    }

    public void delete(Long id) {
        if (!meteringPointRepository.existsById(id)) {
            throw new NeededObjectNotFound("MeteringPoint not found with id: " + id);
        }
        meteringPointRepository.deleteById(id);
    }
}


