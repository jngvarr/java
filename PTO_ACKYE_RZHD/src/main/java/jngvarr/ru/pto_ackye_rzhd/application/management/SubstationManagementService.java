package jngvarr.ru.pto_ackye_rzhd.application.management;

import jngvarr.ru.pto_ackye_rzhd.domain.entities.*;
import jngvarr.ru.pto_ackye_rzhd.domain.repositories.others.*;
import jngvarr.ru.pto_ackye_rzhd.domain.services.SubstationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
public class SubstationManagementService {
    private final RegionRepository regionRepository;
    private final StructuralSubdivisionRepository subdivisionRepository;
    private final PowerSupplyEnterpriseRepository powerSupplyEnterpriseRepository;
    private final PowerSupplyDistrictRepository powerSupplyDistrictRepository;
    private final StationRepository stationRepository;
    private final SubstationService substationService;

    public Substation createSubstationIfNotExists(jngvarr.ru.pto_ackye_rzhd.domain.dto.SubstationDTO dto) {
        String regionName = dto.getRegionName();
        String subdivisionName = dto.getSubdivisionName();
        String powerSupplyEnterpriseName = dto.getPowerSupplyEnterpriseName();
        String districtName = dto.getDistrictName();
        String stationName = dto.getStationName();
        String substationName = dto.getSubstationName();

        Region region = regionRepository.findByName(regionName)
                .orElseGet(() -> {
                    Region r = new Region();
                    r.setName(regionName);
                    log.info("Создан регион: {}", regionName);
                    return regionRepository.save(r);
                });

        StructuralSubdivision subdivision = subdivisionRepository.findByName(subdivisionName)
                .orElseGet(() -> {
                    StructuralSubdivision s = new StructuralSubdivision();
                    s.setName(subdivisionName);
                    s.setRegion(region);
                    log.info("Создано подразделение: {}", subdivisionName);
                    return subdivisionRepository.save(s);
                });

        PowerSupplyEnterprise enterprise = powerSupplyEnterpriseRepository.findByName(powerSupplyEnterpriseName)
                .orElseGet(() -> {
                    PowerSupplyEnterprise e = new PowerSupplyEnterprise();
                    e.setName(powerSupplyEnterpriseName);
                    e.setStructuralSubdivision(subdivision);
                    log.info("Создан участок электроснабжения: {}", powerSupplyEnterpriseName);
                    return powerSupplyEnterpriseRepository.save(e);
                });

        PowerSupplyDistrict district = powerSupplyDistrictRepository.findByName(districtName)
                .orElseGet(() -> {
                    PowerSupplyDistrict d = new PowerSupplyDistrict();
                    d.setName(districtName);
                    d.setPowerSupplyEnterprise(enterprise);
                    log.info("Создан район электроснабжения: {} (предприятие: {})", districtName, powerSupplyEnterpriseName);
                    return powerSupplyDistrictRepository.save(d);
                });

        Station station = stationRepository.findByNameAndPowerSupplyDistrict(stationName, district)
                .orElseGet(() -> {
                    Station s = new Station();
                    s.setName(stationName);
                    s.setPowerSupplyDistrict(district);
                    log.info("Создана станция: {} (район: {})", stationName, districtName);
                    return stationRepository.save(s);
                });

        return substationService.findByName(substationName, station.getName())
                .orElseGet(() -> {
                    Substation s = new Substation();
                    s.setName(substationName);
                    s.setStation(station);
                    log.info("Создана подстанция: {} (станция: {})", substationName, stationName);
                    return substationService.create(s);
                });
    }
}
