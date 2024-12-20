package jngvarr.ru.pto_ackye_rzhd.services;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jngvarr.ru.pto_ackye_rzhd.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughDataException;
import jngvarr.ru.pto_ackye_rzhd.repositories.MeterRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class MeterService {
    private final MeterRepository meterRepository;
    private final EntityManager entityManager;

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

    public void create(Meter meter) {
        if (meter.getId() == null
                && meter.getMeterNumber() != null
                && meter.getMeterModel() != null
                && meter.getDc() != null
        ) {
//                if (meter.getMeteringPoint() != null) {
//                    meter.setMeteringPoint(entityManager.merge(meter.getMeteringPoint())); // Слияние объекта MeteringPoint
//                }
            meterRepository.save(meter);

        } else throw new NotEnoughDataException("Not enough IIK data: " + meter.getId());
    }
}