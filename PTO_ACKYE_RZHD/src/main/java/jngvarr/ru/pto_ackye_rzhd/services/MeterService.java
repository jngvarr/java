package jngvarr.ru.pto_ackye_rzhd.services;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jngvarr.ru.pto_ackye_rzhd.dto.MeterDTO;
import jngvarr.ru.pto_ackye_rzhd.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.mappers.MeterMapper;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughDataException;
import jngvarr.ru.pto_ackye_rzhd.repositories.MeterRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class MeterService {
    private final MeterRepository meterRepository;
    private final EntityManager entityManager;


    public List<MeterDTO> getAll() {
        List<Meter> meters = meterRepository.findAll();

//        List<MeterDTO> meterDTOS = new ArrayList<>();
//        for (Meter m : meters){
//            meterDTOS.add(MeterMapper.toMeterDTO(m));
//        }
//        return meterDTOS;
        return meters.stream()
                .map(MeterMapper::toMeterDTO)
                .collect(Collectors.toList());
    }

    public MeterDTO getMeter(Long id) {
        Optional<Meter> neededMeter = meterRepository.findById(id);
        if (neededMeter.isPresent()) {
            return MeterMapper.toMeterDTO(neededMeter.get());
        } else throw new NeededObjectNotFound("MeteringPoint not found: " + id);
    }

    @Transactional
    public void saveAll(List<Meter> meters) {
        // Приводим связанные сущности (например, Dc) в актуальное состояние
//        for (Meter meter : meters) {
//            if (meter.getDc() != null) {
//                meter.setDc(entityManager.merge(meter.getDc())); // Слияние объекта Dc
//            }
//        }
        // Сохраняем все объекты Meter
        meterRepository.saveAll(meters);
    }

    public Meter create(MeterDTO meter) {
        if (meter.getId() == null
                && meter.getMeterNumber() != null
                && meter.getMeterModel() != null
                && meter.getDcNum() != null
        ) {
//                if (meter.getMeteringPoint() != null) {
//                    meter.setMeteringPoint(entityManager.merge(meter.getMeteringPoint())); // Слияние объекта MeteringPoint
//                }
            return meterRepository.save(MeterMapper.fromMeterDTO(meter));

        } else throw new NotEnoughDataException("Not enough IIK data: " + meter.getId());
    }

    public void create(Meter meter) {
        if (meter.getId() == null
                && meter.getMeterNumber() != null
                && meter.getMeterModel() != null
                && meter.getDc() != null
        ) {
            meterRepository.save(meter);
        } else throw new NotEnoughDataException("Not enough IIK data: " + meter.getId());
    }
}