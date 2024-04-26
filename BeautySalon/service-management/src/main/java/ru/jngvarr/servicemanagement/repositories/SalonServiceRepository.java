package ru.jngvarr.servicemanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dao.entities.Servize;

import java.util.List;

@Repository
public interface SalonServiceRepository extends JpaRepository<Servize, Long> {
    List<Servize> findAllByTitle(String title);
    List<Servize> findAllByDescriptionContainingIgnoreCase(String description);
}
