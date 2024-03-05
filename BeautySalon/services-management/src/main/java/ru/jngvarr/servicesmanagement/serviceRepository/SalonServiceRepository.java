package ru.jngvarr.servicesmanagement.serviceRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jngvarr.beautysalon.model.salon_services.Servize;

@Repository
public interface SalonServiceRepository extends JpaRepository<Servize,Long>{
}