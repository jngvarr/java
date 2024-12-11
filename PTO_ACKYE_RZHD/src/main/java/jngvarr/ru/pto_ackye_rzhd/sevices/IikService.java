package jngvarr.ru.pto_ackye_rzhd.sevices;

import jngvarr.ru.pto_ackye_rzhd.entities.Iik;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughData;
import jngvarr.ru.pto_ackye_rzhd.repositories.IikRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Service
@Slf4j
@RequiredArgsConstructor
public class IikService {
    static final String ERROR_TEXT = "Error occurred: ";
    private final IikRepository iikRepository;

    public List<Iik> getIIKs() {
        return iikRepository.findAll();
    }

    public Iik getIik(Long id) {
        Optional<Iik> neededIik = iikRepository.findById(id);
        return neededIik.orElseThrow(() -> new NeededObjectNotFound("Iik not found: " + id));
    }

//    public Iik getIikByMeterNum(Integer meterNumber) {
//        Iik iik = iikRepository.findByMeterNumber(meterNumber);
//        if (iik != null) return iik;
//        else throw new NeededObjectNotFound("Iik with such meter number not found: " + meterNumber);
//    }

//    public List<Iik> iiksByDcNumber(Integer dcNumber) {
//        List<Iik> iiks = iikRepository.findByDcNumber(dcNumber);
//        if (iiks != null) return iiks;
//        else throw new NeededObjectNotFound("Iiks with such DC number not found: " + dcNumber);
//    }

    public Iik createIik(Iik iikToCreate) {
        if ((iikToCreate.getId() != null &&
                iikToCreate.getRegion() != null &&
                iikToCreate.getEel() != null &&
                iikToCreate.getEch() != null &&
                iikToCreate.getEcheOrEchk() != null &&
                iikToCreate.getStation() != null
//                &&
//                iikToCreate.getSubstation() != null &&
//                iikToCreate.getMeteringPoint() != null &&
//                iikToCreate.getMeteringPointAddress() != null &&
//                iikToCreate.getMeterModel() != null &&
//                iikToCreate.getMeterNumber() != null &&
//                iikToCreate.getDcNumber() != null &&
//                iikToCreate.getInstallationDate() != null
        )) {
            return iikRepository.save(iikToCreate);
        } else throw new NotEnoughData("Not enough IIK data");
    }

    public Iik updateIik(Iik newData, Long iikId) {
        Optional<Iik> oldIik = iikRepository.findById(iikId);
        if (oldIik.isPresent()) {
            Iik newIik = oldIik.get();
            if (newData.getRegion() != null) newIik.setRegion(newData.getRegion());
            if (newData.getEel() != null) newIik.setEel(newData.getEel());
            if (newData.getEch() != null) newIik.setEch(newData.getEch());
            if (newData.getEcheOrEchk() != null) newIik.setEcheOrEchk(newData.getEcheOrEchk());
            if (newData.getStation() != null) newIik.setStation(newData.getStation());
//            if (newData.getSubstation() != null) newIik.setSubstation(newData.getSubstation());
//            if (newData.getMeteringPoint() != null) newIik.setMeteringPoint(newData.getMeteringPoint());
//            if (newData.getMeteringPointAddress() != null)
//                newIik.setMeteringPointAddress(newData.getMeteringPointAddress());
//            if (newData.getMeterModel() != null) newIik.setMeterModel(newData.getMeterModel());
//            if (newData.getMeterNumber() != null) newIik.setMeterNumber(newData.getMeterNumber());
//            if (newData.getDcNumber() != null) newIik.setDcNumber(newData.getDcNumber());
//            if (newData.getInstallationDate() != null) newIik.setInstallationDate(newData.getInstallationDate());

            return iikRepository.save(newIik);
        } else throw new IllegalArgumentException("Iik not found");
    }

    public void delete(Long iikId) {
        iikRepository.deleteById(iikId);
    }
}