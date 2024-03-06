package ru.jngvarr.servicemamagement.serviceRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jngvarr.servicemamagement.model.Servize;

@Repository
public interface SalonServiceRepository extends JpaRepository<Servize,Long>{
}