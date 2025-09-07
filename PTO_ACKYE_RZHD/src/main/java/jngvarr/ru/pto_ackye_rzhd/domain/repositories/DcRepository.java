package jngvarr.ru.pto_ackye_rzhd.domain.repositories;

import jngvarr.ru.pto_ackye_rzhd.domain.entities.Dc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DcRepository extends JpaRepository<Dc,Long> {
    boolean existsByDcNumber(String num);

    Optional<Dc> getDcByDcNumber(String num);

    @Query("SELECT d FROM Dc d LEFT JOIN FETCH d.meters WHERE d.dcNumber = :dcNumber")
    Optional<Dc> findByDcNumberWithMeters(@Param("dcNumber") String dcNumber);
}
