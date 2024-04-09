package ru.jngvarr.storagemanagement.repositories;

import dao.Consumable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Consumable, Long> {
}
