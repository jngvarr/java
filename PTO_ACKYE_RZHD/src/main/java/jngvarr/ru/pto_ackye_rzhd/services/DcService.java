package jngvarr.ru.pto_ackye_rzhd.services;

import jakarta.transaction.Transactional;
import jngvarr.ru.pto_ackye_rzhd.entities.Dc;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.repositories.DcRepository;
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
public class DcService {
    private final DcRepository ivkeRepository;


    public List<Dc> getDCs() {
        return ivkeRepository.findAll();
    }

    public Dc getDcById(Long id) {
        Optional<Dc> neededDc = ivkeRepository.findById(id);
        return neededDc.orElseThrow(() -> new NeededObjectNotFound("Dc not found: " + id));
    }
    public Dc getDcByNumber(String num) {
        Optional<Dc> neededDc = ivkeRepository.getDcByDcNumber(num);
        return neededDc.orElseThrow(() -> new NeededObjectNotFound("Dc not found: " + num));
    }

//    public Dc getDcByDcNum(Integer dcNumber) {
//        Dc iik = ivkeRepository.findByDcNumber(dcNumber);
//        if (iik != null) return iik;
//        else throw new NeededObjectNotFound("Dc with such meter number not found: " + dcNumber);
//    }

    public Dc createDc(Dc dcToCreate) {
        boolean dcIsExists = ivkeRepository.existsByDcNumber(dcToCreate.getDcNumber());
        if (dcToCreate.getId() == null
                && dcToCreate.getDcNumber() != null
                && dcToCreate.getDcModel() != null
                && !dcIsExists
        ) {
            return ivkeRepository.save(dcToCreate);
        }
        return null;
    }

    @Transactional
    public void createAll(List<Dc> ivke) {
        ivkeRepository.saveAll(ivke);
    }

    public Dc updateDc(Dc newData, Long ivkeId) {
        Optional<Dc> oldDc = ivkeRepository.findById(ivkeId);
        if (oldDc.isPresent()) {
            Dc newDc = oldDc.get();
            if (newData.getSubstation() != null) newDc.setSubstation(newData.getSubstation());
            if (newData.getBusSection() != 0) newDc.setBusSection(newData.getBusSection());
            if (newData.getDcNumber() != null) newDc.setDcNumber(newData.getDcNumber());
            if (newData.getDcModel() != null) newDc.setDcModel(newData.getDcModel());
            if (newData.getInstallationDate() != null) newDc.setInstallationDate(newData.getInstallationDate());
            if (newData.getManufactureDate() != null) newDc.setManufactureDate(newData.getManufactureDate());

            return ivkeRepository.save(newDc);
        } else throw new IllegalArgumentException("Dc not found");
    }

    public void delete(Long iikId) {
        ivkeRepository.deleteById(iikId);
    }
}