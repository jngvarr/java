package ru.jngvarr.storagemanagement.repositories;

import dao.entities.Consumable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorageRepository extends JpaRepository<Consumable, Long> {

    List<Consumable> getConsumablesByTitle(String title);
}
