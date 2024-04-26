package ru.jngvarr.servicemanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import dao.entities.Servize;

import java.util.List;

@Repository
public interface SalonServiceRepository extends JpaRepository<Servize, Long> {
    List<Servize> findAllByTitle(String title);
    List<Servize> findAllByDescriptionContainingIgnoreCase(String description);

    @Query("SELECT DISTINCT s FROM Servize s JOIN s.consumables c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :consumableTitle, '%'))")
    List<Servize> findByConsumableTitleContaining(String consumableTitle);


}
