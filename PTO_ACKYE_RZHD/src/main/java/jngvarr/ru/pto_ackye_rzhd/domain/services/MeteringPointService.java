package jngvarr.ru.pto_ackye_rzhd.domain.services;

import jngvarr.ru.pto_ackye_rzhd.domain.dto.MeteringPointDTO;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.application.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.application.exceptions.NotEnoughDataException;
import jngvarr.ru.pto_ackye_rzhd.application.mappers.MeterMapper;
import jngvarr.ru.pto_ackye_rzhd.domain.repositories.MeteringPointRepository;
import jngvarr.ru.pto_ackye_rzhd.domain.repositories.others.SubstationRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class MeteringPointService {
    private final MeteringPointRepository meteringPointRepository;
    private Long id = 100000000000L;
    private final SubstationRepository substationRepository;

    public List<MeteringPointDTO> getAll() {
        List<MeteringPoint> meteringPoints = meteringPointRepository.findAll();
        return meteringPoints.stream()
                .map(MeterMapper::toMeteringPointDTO)
                .collect(Collectors.toList());
    }

    public List<MeteringPoint> getAllIik() {
        return meteringPointRepository.findAll();
    }

    public boolean isIikExists(long id) {
        return meteringPointRepository.existsById(id);
    }

    public MeteringPointDTO getIik(Long id) {
        Optional<MeteringPoint> neededMeteringPoint = meteringPointRepository.findById(id);
        if (neededMeteringPoint.isPresent()) {
            return MeterMapper.toMeteringPointDTO(neededMeteringPoint.get());
        } else throw new NeededObjectNotFound("MeteringPoint not found: " + id);
    }

    public MeteringPointDTO create(MeteringPointDTO iik) {
        if (iik.getName() != null
                && iik.getMeteringPointAddress() != null
                && iik.getSubstation() != null) {
            MeteringPoint m = MeterMapper.fromMeteringPointDTO(iik);
            substationRepository.save(m.getSubstation());
            m.setId(getExternalId());
            meteringPointRepository.save(m);
            return MeterMapper.toMeteringPointDTO(m);
        } else {
            throw new NotEnoughDataException("Not enough data to create MeteringPoint");
        }
    }

    public Long getExternalId() {
        return id++;
    }

    // TODO Временный метод, на время разработки. id брать из выгрузки
    public Long getNextId() {
        return meteringPointRepository.findMaxId() + 1;
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

    //    @Transactional !!
    public List<MeteringPoint> createIiks(List<MeteringPoint> iiks) {
        for (MeteringPoint iik : iiks) {
            if (iik.getName() == null || iik.getMeteringPointAddress() == null || iik.getSubstation() == null) {
                throw new NotEnoughDataException("Not enough data to create one or more MeteringPoints");
            }
        }
        return meteringPointRepository.saveAll(iiks);
    }

    public MeteringPoint update(MeteringPoint newData, Long iikId) {
        Optional<MeteringPoint> oldIik = meteringPointRepository.findById(iikId);
        if (oldIik.isPresent()) {
            MeteringPoint newIik = oldIik.get();
            if (newData.getName() != null) newIik.setName(newData.getName());
            if (newData.getMeterPlacement() != null) newIik.setMeterPlacement(newData.getMeterPlacement());
            if (newData.getMeteringPointAddress() != null)
                newIik.setMeteringPointAddress(newData.getMeteringPointAddress());
            if (newData.getInstallationDate() != null) newIik.setInstallationDate(newData.getInstallationDate());
            if (newData.getConnection() != null) newIik.setConnection(newData.getConnection());
            if (newData.getSubstation() != null) newIik.setSubstation(newData.getSubstation());
            if (newData.getMeter() != null) newIik.setMeter(newData.getMeter());

            return meteringPointRepository.save(newIik);
        } else throw new IllegalArgumentException("MeteringPoint not found");
    }

    public void delete(Long id) {
        if (!meteringPointRepository.existsById(id)) {
            throw new NeededObjectNotFound("MeteringPoint not found with id: " + id);
        }
        meteringPointRepository.deleteById(id);
    }

    public MeteringPoint getIikByMeterId(Long deviceId) {
        return meteringPointRepository.findByMeterId(deviceId);
    }
}


