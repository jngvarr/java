package jngvarr.ru.pto_ackye_rzhd.domain.repositories.others;

import jngvarr.ru.pto_ackye_rzhd.domain.entities.StructuralSubdivision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StructuralSubdivisionRepository extends JpaRepository<StructuralSubdivision, Long> {
    Optional<StructuralSubdivision> findByName(String subdivisionName);
}
