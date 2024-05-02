package ru.jngvarr.appointmentmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jngvarr.appointmentmanagement.model.VisitData;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<VisitData, Long> {
    List<VisitData> findAllVisitsByClientId(Long clientId);

    List<VisitData> findAllVisitsByEmployeeId(Long employeeId);

    List<VisitData> findAllVisitsByServiceId(Long serviceId);

    List<VisitData> findAllVisitsByVisitDate(LocalDate date);

//    List<Visit> findByLastName(String lastName);
//
//    List<Visit> findByContact(String contact);
}
