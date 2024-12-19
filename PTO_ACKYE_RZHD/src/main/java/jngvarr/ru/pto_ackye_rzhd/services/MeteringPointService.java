package jngvarr.ru.pto_ackye_rzhd.services;

import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.repositories.MeteringPointRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

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
        if
        return meteringPointRepository.save(iik);
    }

    public List<MeteringPoint> createIiks(List<MeteringPoint> iiks) {
        return meteringPointRepository.saveAll(iiks);
    }

    public MeteringPoint update(MeteringPoint newData, Long id) {
        return meteringPointRepository.save(newData, id);
    }

    public void delete(Long id) {
        meteringPointRepository.delete(id);
    }
}

}
