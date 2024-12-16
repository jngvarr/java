package jngvarr.ru.pto_ackye_rzhd.sevices;

import jngvarr.ru.pto_ackye_rzhd.entities.DcComplex;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughData;
import jngvarr.ru.pto_ackye_rzhd.repositories.DcComplexRepository;
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
public class IvkeService {
    private final DcComplexRepository ivkeRepository;

    public List<DcComplex> getDCs() {
        return ivkeRepository.findAll();
    }

    public DcComplex getDc(Long id) {
        Optional<DcComplex> neededDc = ivkeRepository.findById(id);
        return neededDc.orElseThrow(() -> new NeededObjectNotFound("Dc not found: " + id));
    }

    public DcComplex getDcByDcNum(Integer dcNumber) {
        DcComplex iik = ivkeRepository.findByDcNumber(dcNumber);
        if (iik != null) return iik;
        else throw new NeededObjectNotFound("Dc with such meter number not found: " + dcNumber);
    }

    public DcComplex createDc(DcComplex ivkeToCreate) {
        if ((ivkeToCreate.getId() != null &&
                ivkeToCreate.getRegion() != null &&
                ivkeToCreate.getEel() != null &&
                ivkeToCreate.getEch() != null &&
                ivkeToCreate.getEcheOrEchk() != null &&
                ivkeToCreate.getStation() != null &&
                ivkeToCreate.getSubstation() != null &&
                ivkeToCreate.getDcNumber() != null &&
                ivkeToCreate.getDcInstallationDate() != null &&
                ivkeToCreate.getNumberOfMeters() != null
        )) {
            return ivkeRepository.save(ivkeToCreate);
        } else throw new NotEnoughData("Not enough IIK data");
    }

    public void createAll(List<DcComplex> ivke) {
        ivkeRepository.saveAll(ivke);
    }

    public DcComplex updateDc(DcComplex newData, Long ivkeId) {
        Optional<DcComplex> oldDc = ivkeRepository.findById(ivkeId);
        if (oldDc.isPresent()) {
            DcComplex newDc = oldDc.get();
            if (newData.getRegion() != null) newDc.setRegion(newData.getRegion());
            if (newData.getEel() != null) newDc.setEel(newData.getEel());
            if (newData.getEch() != null) newDc.setEch(newData.getEch());
            if (newData.getEcheOrEchk() != null) newDc.setEcheOrEchk(newData.getEcheOrEchk());
            if (newData.getStation() != null) newDc.setStation(newData.getStation());
            if (newData.getSubstation() != null) newDc.setSubstation(newData.getSubstation());
            if (newData.getDcNumber() != null) newDc.setDcNumber(newData.getDcNumber());
            if (newData.getDcInstallationDate() != null) newDc.setDcInstallationDate(newData.getDcInstallationDate());

            return ivkeRepository.save(newDc);
        } else throw new IllegalArgumentException("Dc not found");
    }

    public void delete(Long iikId) {
        ivkeRepository.deleteById(iikId);
    }
}