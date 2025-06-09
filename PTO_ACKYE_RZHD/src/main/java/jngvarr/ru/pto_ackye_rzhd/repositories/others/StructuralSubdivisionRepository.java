package jngvarr.ru.pto_ackye_rzhd.repositories.others;

import jngvarr.ru.pto_ackye_rzhd.entities.StructuralSubdivision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StructuralSubdivisionRepository extends JpaRepository<StructuralSubdivision, Long> {
    Optional<StructuralSubdivision> findByName(String subdivisionName);
}
