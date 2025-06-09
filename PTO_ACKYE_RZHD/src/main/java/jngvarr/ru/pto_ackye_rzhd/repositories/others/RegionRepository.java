package jngvarr.ru.pto_ackye_rzhd.repositories.others;

import jngvarr.ru.pto_ackye_rzhd.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByName(String regionName);
}
