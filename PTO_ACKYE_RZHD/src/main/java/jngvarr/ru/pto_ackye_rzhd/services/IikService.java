package jngvarr.ru.pto_ackye_rzhd.services;

import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughDataException;
import jngvarr.ru.pto_ackye_rzhd.repositories.MeteringPointRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Data
@Service
@Slf4j
@RequiredArgsConstructor
public class IikService {
    static final String ERROR_TEXT = "Error occurred: ";
    private final MeteringPointRepository iikRepository;


    public List<MeteringPoint> getIIKs() {
        return iikRepository.findAll();
    }

    public MeteringPoint getIik(Long id) {
        Optional<MeteringPoint> neededIik = iikRepository.findById(id);
        return neededIik.orElseThrow(() -> new NeededObjectNotFound("MeteringPoint not found: " + id));
    }

//    public MeteringPoint getIikByMeterNum(Integer meterNumber) {
//        MeteringPoint iik = iikRepository.findByMeterNumber(meterNumber);
//        if (iik != null) return iik;
//        else throw new NeededObjectNotFound("MeteringPoint with such meter number not found: " + meterNumber);
//    }

//    public List<MeteringPoint> iiksByDcNumber(Integer dcNumber) {
//        List<MeteringPoint> iiks = iikRepository.findByDcNumber(dcNumber);
//        if (iiks != null) return iiks;
//        else throw new NeededObjectNotFound("Iiks with such DC number not found: " + dcNumber);
//    }


    public MeteringPoint createIik(MeteringPoint iikToCreate) {
        if ((iikToCreate.getId() != null &&
                iikToCreate.getSubstation() != null &&
                iikToCreate.getMeteringPointAddress() != null &&
                iikToCreate.getName() != null
        )) {
            return iikRepository.save(iikToCreate);
        } else throw new NotEnoughDataException("Not enough IIK data");
    }

    @Transactional
    public List<MeteringPoint> createAll(List<MeteringPoint> iiks) {
        return getIikRepository().saveAll(iiks);
    }

    public MeteringPoint updateIik(MeteringPoint newData, Long iikId) {
        Optional<MeteringPoint> oldIik = iikRepository.findById(iikId);
        if (oldIik.isPresent()) {
            MeteringPoint newIik = oldIik.get();
            if (newData.getName() != null) newIik.setName(newData.getName());
            if (newData.getMeterPlacement() != null) newIik.setMeterPlacement(newData.getMeterPlacement());
            if (newData.getMeteringPointAddress() != null) newIik.setMeteringPointAddress(newData.getMeteringPointAddress());
            if (newData.getInstallationDate() != null) newIik.setInstallationDate(newData.getInstallationDate());
            if (newData.getConnection() != null) newIik.setConnection(newData.getConnection());
            if (newData.getSubstation() != null) newIik.setSubstation(newData.getSubstation());

            return iikRepository.save(newIik);
        } else throw new IllegalArgumentException("MeteringPoint not found");
    }

    public void delete(Long iikId) {
        iikRepository.deleteById(iikId);
    }
}