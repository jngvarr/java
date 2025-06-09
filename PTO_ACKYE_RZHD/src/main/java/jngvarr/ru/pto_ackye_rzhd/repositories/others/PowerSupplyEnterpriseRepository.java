package jngvarr.ru.pto_ackye_rzhd.repositories.others;

import jngvarr.ru.pto_ackye_rzhd.entities.PowerSupplyEnterprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PowerSupplyEnterpriseRepository extends JpaRepository<PowerSupplyEnterprise, Long> {
    Optional<PowerSupplyEnterprise> findByName(String subdivisionName);
}
