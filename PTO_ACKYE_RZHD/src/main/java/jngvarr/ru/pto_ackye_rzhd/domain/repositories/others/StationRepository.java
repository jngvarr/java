package jngvarr.ru.pto_ackye_rzhd.domain.repositories.others;

import jngvarr.ru.pto_ackye_rzhd.domain.entities.PowerSupplyDistrict;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
   Optional<Station> findByNameAndPowerSupplyDistrict(String name, PowerSupplyDistrict district);
}
