package jngvarr.ru.pto_ackye_rzhd.domain.repositories;

import jngvarr.ru.pto_ackye_rzhd.domain.entities.MeteringPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MeteringPointRepository extends JpaRepository<MeteringPoint, Long> {
    MeteringPoint findByMeterId(Long meterId);

    @Query("SELECT COALESCE(MAX(mp.id), 0) FROM MeteringPoint mp")
    Long findMaxId();
}
