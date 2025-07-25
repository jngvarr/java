package jngvarr.ru.pto_ackye_rzhd.repositories.others;

import jngvarr.ru.pto_ackye_rzhd.entities.Substation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubstationRepository extends JpaRepository<Substation, Long> {

    Optional<Substation> findByNameAndAndStationName(String name, String stationName);

}
