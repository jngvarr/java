package jngvarr.ru.pto_ackye_rzhd.repositories.others;

import jngvarr.ru.pto_ackye_rzhd.entities.PowerSupplyDistrict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PowerSupplyDistrictRepository extends JpaRepository<PowerSupplyDistrict,Long> {
    Optional<PowerSupplyDistrict> findByName(String districtName);
}
