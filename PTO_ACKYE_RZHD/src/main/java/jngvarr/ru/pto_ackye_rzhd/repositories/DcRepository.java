package jngvarr.ru.pto_ackye_rzhd.repositories;

import jngvarr.ru.pto_ackye_rzhd.entities.Dc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DcRepository extends JpaRepository<Dc,Long> {
    boolean existsByDcNumber(String num);

    Optional<Dc> getDcByDcNumber(String num);
}
