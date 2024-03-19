package ru.jngvarr.servicemamagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jngvarr.servicemamagement.model.Service;

@Repository
public interface SalonServiceRepository extends JpaRepository<Service,Long>{
}