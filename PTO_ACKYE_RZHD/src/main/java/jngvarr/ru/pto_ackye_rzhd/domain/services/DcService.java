package jngvarr.ru.pto_ackye_rzhd.domain.services;

import jngvarr.ru.pto_ackye_rzhd.domain.dto.DcDTO;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Dc;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Substation;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughDataException;
import jngvarr.ru.pto_ackye_rzhd.mappers.MeterMapper;
import jngvarr.ru.pto_ackye_rzhd.domain.repositories.DcRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Service
@Slf4j
@RequiredArgsConstructor
public class DcService {
    private final DcRepository dcRepository;
    private final SubstationService substationService;


    public List<DcDTO> getAll() {
        List<Dc> dcs = dcRepository.findAll();
        return dcs.stream()
                .map(MeterMapper::toDcDTO)
                .collect(Collectors.toList());
    }

    public List<Dc> getAllDc() {
        return dcRepository.findAll();
    }

    public DcDTO getDcById(Long id) {
        Optional<Dc> neededDc = dcRepository.findById(id);
        return MeterMapper.toDcDTO(neededDc.orElseThrow(() -> new NeededObjectNotFound("Dc not found: " + id)));
    }

//    @Transactional
//    public Dc getDcByNumber(String num) {
//        Optional<Dc> neededDc = dcRepository.getDcByDcNumber(num);
//        return neededDc.orElseThrow(() -> new NeededObjectNotFound("Dc not found: " + num));
//    }

    //    @Transactional(readOnly = true)
    public Dc getDcByNumber(String dcNumber) {
        return dcRepository.findByDcNumberWithMeters(dcNumber)
                .orElseGet(() -> {
                    log.warn("Dc с номером {} не найден", dcNumber);
                    return null;
                });
    }

    public DcDTO getDcDTOByNumber(String num) {
        Optional<Dc> neededDc = dcRepository.getDcByDcNumber(num);
        return MeterMapper.toDcDTO(neededDc.orElseThrow(() -> new NeededObjectNotFound("Dc not found: " + num)));
    }

    public DcDTO createDc(DcDTO dcToCreate) {
        boolean dcIsExists = dcRepository.existsByDcNumber(dcToCreate.getDcNumber());
        if (dcIsExists) throw new IllegalArgumentException("Dc with such number already exists!");
        else if (dcToCreate.getDcNumber() != null
//                && dcToCreate.getDcModel() != null
                && dcToCreate.getSubstationId() != null

        ) {
            Dc dc = MeterMapper.fromDcDTO(dcToCreate);
            dc.setSubstation(updateSubstation(dcToCreate.getSubstationId()));
            return MeterMapper.toDcDTO(dcRepository.save(dc));
        } else throw new NotEnoughDataException("Not enough DC data: " + dcToCreate.getId());
    }

    public void createDc(Dc dcToCreate) {
        boolean dcIsExists = dcRepository.existsByDcNumber(dcToCreate.getDcNumber());
        if (dcToCreate.getDcNumber() != null
                && !dcIsExists)
            dcRepository.save(dcToCreate);
    }


    //    @Transactional
    public void createAll(List<Dc> ivke) {
        dcRepository.saveAll(ivke);
    }

    public DcDTO updateDc(DcDTO newData, Long ivkeId) {
        Optional<Dc> oldDc = dcRepository.findById(ivkeId);
        if (oldDc.isPresent()) {
            Dc newDc = oldDc.get();
            if (newData.getBusSection() != 0) newDc.setBusSection(newData.getBusSection());
            if (newData.getDcNumber() != null) newDc.setDcNumber(newData.getDcNumber());
            if (newData.getDcModel() != null) newDc.setDcModel(newData.getDcModel());
            if (newData.getInstallationDate() != null) newDc.setInstallationDate(newData.getInstallationDate());
            if (newData.getManufactureDate() != null) newDc.setManufactureDate(newData.getManufactureDate());
            if (newData.getSubstationId() != null) {
                newDc.setSubstation(updateSubstation(newData.getSubstationId()));
            }
            return MeterMapper.toDcDTO(dcRepository.save(newDc));
        } else throw new IllegalArgumentException("Dc not found");
    }

    public Dc updateDc(Dc newData, Long ivkeId) {
        Optional<Dc> oldDc = dcRepository.findById(ivkeId);
        if (oldDc.isPresent()) {
            Dc newDc = oldDc.get();
            if (newData.getBusSection() != 0) newDc.setBusSection(newData.getBusSection());
            if (newData.getDcNumber() != null) newDc.setDcNumber(newData.getDcNumber());
            if (newData.getDcModel() != null) newDc.setDcModel(newData.getDcModel());
            if (newData.getInstallationDate() != null) newDc.setInstallationDate(newData.getInstallationDate());
            if (newData.getManufactureDate() != null) newDc.setManufactureDate(newData.getManufactureDate());
            if (newData.getSubstation() != null) {
                newDc.setSubstation(updateSubstation(newData.getSubstation().getId()));
            }
            return dcRepository.save(newDc);
        } else throw new IllegalArgumentException("Dc not found");
    }

    public void delete(Long iikId) {
        dcRepository.deleteById(iikId);
    }

    private Substation updateSubstation(Long id) {
        Substation newDcSubstation = new Substation();
        try {
            newDcSubstation = substationService.getSubstationById(id);
        } catch (Exception e) {
            log.error("Something went wrong {}: ", e.getMessage());
        }
        return newDcSubstation;
    }

}