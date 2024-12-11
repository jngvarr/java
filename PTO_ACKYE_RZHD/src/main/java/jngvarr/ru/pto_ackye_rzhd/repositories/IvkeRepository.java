package jngvarr.ru.pto_ackye_rzhd.repositories;

import jngvarr.ru.pto_ackye_rzhd.entities.Iik;
import jngvarr.ru.pto_ackye_rzhd.entities.Ivke;
import jngvarr.ru.pto_ackye_rzhd.entities.others.DC;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IvkeRepository extends JpaRepository<Ivke,Long> {
    public Ivke findByDcNumber(Integer dcNumber);
}
