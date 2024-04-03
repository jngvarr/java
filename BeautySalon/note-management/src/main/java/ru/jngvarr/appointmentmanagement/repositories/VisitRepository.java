package ru.jngvarr.appointmentmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dao.Visit;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByClientId(Long clientId);

    List<Visit> findByEmployeeId(Long employeeId);

//    List<Visit> findByLastName(String lastName);
//
//    List<Visit> findByContact(String contact);
}
