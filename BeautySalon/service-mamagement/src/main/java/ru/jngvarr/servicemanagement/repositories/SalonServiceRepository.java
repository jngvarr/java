package ru.jngvarr.servicemanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dao.Service;

@Repository
public interface SalonServiceRepository extends JpaRepository<Service,Long>{
}