package jngvarr.ru.pto_ackye_rzhd.repositories;

import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeteringPointRepository extends JpaRepository<MeteringPoint, Long> {
}
