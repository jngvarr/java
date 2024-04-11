package ru.jngvarr.storagemanagement.service;

import dao.entities.Consumable;
import exceptions.NeededObjectNotFound;
import exceptions.NotEnoughData;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.jngvarr.storagemanagement.repositories.StorageRepository;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository repository;

    public List<Consumable> getConsumables() {
        log.debug("get");
        return repository.findAll();
    }

    public Consumable getConsumable(Long id) {
        Optional<Consumable> neededConsumable = repository.findById(id);
        if (neededConsumable.isPresent()) return neededConsumable.get();
        else throw new NeededObjectNotFound("Consumable not found");
    }

    public Consumable add(Consumable consumable) {
        if (consumable.getTitle() != null & consumable.getUnit() != null) return repository.save(consumable);
        else throw new NotEnoughData("Not enough consumable data");
    }

    public Consumable update(Consumable newData, Long id) {
        Optional<Consumable> oldConsumable = repository.findById(id);
        if (oldConsumable.isPresent()) {
            Consumable newConsumable = oldConsumable.get();
            if (newData.getTitle() != null) newConsumable.setTitle(newData.getTitle());
            if (newData.getUnit() != null) newConsumable.setUnit(newConsumable.getUnit());
//            log.debug("update {}", newConsumable);
            return repository.save(newConsumable);
        } else throw new NeededObjectNotFound("Consumable not found");
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

