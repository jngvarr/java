package jngvarr.ru.pto_ackye_rzhd.domain.repositories;

import jngvarr.ru.pto_ackye_rzhd.domain.entities.Meter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {
    Optional<Meter> findByMeterNumber(String num);
}
