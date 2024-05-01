package ru.jngvarr.appointmentmanagement.repositories;

import dao.entities.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jngvarr.appointmentmanagement.model.VisitData;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<VisitData, Long> {
    List<VisitData> findByClientId(Long clientId);

    List<VisitData> findByEmployeeId(Long employeeId);

    List<VisitData>findAllByVisitDate(LocalDate date);

//    List<Visit> findByLastName(String lastName);
//
//    List<Visit> findByContact(String contact);
}
