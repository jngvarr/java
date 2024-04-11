package ru.jngvarr.storagemanagement.repositories;

import dao.entities.Consumable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Consumable, Long> {
}
