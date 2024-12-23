package jngvarr.ru.pto_ackye_rzhd.services;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jngvarr.ru.pto_ackye_rzhd.dto.MeterDTO;
import jngvarr.ru.pto_ackye_rzhd.entities.Dc;
import jngvarr.ru.pto_ackye_rzhd.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.mappers.MeterMapper;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughDataException;
import jngvarr.ru.pto_ackye_rzhd.repositories.MeterRepository;
import jngvarr.ru.pto_ackye_rzhd.repositories.MeteringPointRepository;
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
    private final MeteringPointRepository meteringPointRepository;
    //    private final EntityManager entityManager;
    private final DcService dcService;


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

    public MeterDTO create(MeterDTO meterDTO) {
        if (meterDTO.getId() == null
                && meterDTO.getMeterNumber() != null
                && meterDTO.getMeterModel() != null
                && meterDTO.getDcNum() != null
        ) {
//                if (meterDTO.getMeteringPoint() != null) {
//                    meterDTO.setMeteringPoint(entityManager.merge(meterDTO.getMeteringPoint())); // Слияние объекта MeteringPoint
//                }
            Dc dc = new Dc();
            try {
                dc = dcService.getDcByNumber(meterDTO.getDcNum());
            } catch (Exception e) {
                log.error("Something went wrong {}", e.getMessage());
            }
            Meter meter = MeterMapper.fromMeterDTO(meterDTO);
            meter.setDc(dc);
            dc.getMeters().add(meter);
            return MeterMapper.toMeterDTO(meterRepository.save(meter));
        } else throw new NotEnoughDataException("Not enough IIK data: " + meterDTO.getId());
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

//    public MeterDTO update(MeterDTO newData, Long id) {
//        Optional<Meter> oldMeter = meterRepository.findById(id);
//        if (oldMeter.isPresent()) {
//            Meter newMeter = oldMeter.get();
//            if (newData.getMeterNumber() != null) newMeter.setMeterNumber(newData.getMeterNumber());
//            if (newData.getMeterModel() != null) newMeter.setMeterModel(newData.getMeterModel());
////            if (newData.getMeteringPointId() != newMeter.getMeteringPoint().getId()){
////
////                newMeter.setMeteringPoint(meteringPointRepository.newData.getMeteringPointId());
////            }
////            if (newData.getInstallationDate() != null) newMeter.setInstallationDate(newData.getInstallationDate());
////            if (newData.getConnection() != null) newMeter.setConnection(newData.getConnection());
////            if (newData.getSubstation() != null) newMeter.setSubstation(newData.getSubstation());
//
//            return meterRepository.save(newMeter);
//        } else throw new IllegalArgumentException("MeteringPoint not found");
//    }
}