package jngvarr.ru.pto_ackye_rzhd.services;

import jngvarr.ru.pto_ackye_rzhd.dto.MeteringPointDTO;
import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughDataException;
import jngvarr.ru.pto_ackye_rzhd.mappers.MeterMapper;
import jngvarr.ru.pto_ackye_rzhd.repositories.MeteringPointRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class MeteringPointService {
    private final MeteringPointRepository meteringPointRepository;

    public List<MeteringPointDTO> getAll() {
        List<MeteringPoint> meteringPoints = meteringPointRepository.findAll();
        return meteringPoints.stream()
                .map(MeterMapper::toMeteringPointDTO)
                .collect(Collectors.toList());
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
            return MeterMapper.toMeteringPointDTO(meteringPointRepository.save(MeterMapper.fromMeteringPointDTO(iik)));
        } else {
            throw new NotEnoughDataException("Not enough data to create MeteringPoint");
        }
    }

    @Transactional
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

            return meteringPointRepository.save(newIik);
        } else throw new IllegalArgumentException("MeteringPoint not found");
    }

    public void delete(Long id) {
        if (!meteringPointRepository.existsById(id)) {
            throw new NeededObjectNotFound("MeteringPoint not found with id: " + id);
        }
        meteringPointRepository.deleteById(id);
    }
}


