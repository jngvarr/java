package jngvarr.ru.pto_ackye_rzhd.sevices;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jngvarr.ru.pto_ackye_rzhd.entities.*;
import org.springframework.stereotype.Service;

@Service
public class EntitySaveService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Substation saveSubstation(Substation substation) {
        // Проверка существования всех вложенных сущностей перед сохранением
        Station station = findOrPersistStation(substation.getStation());
        substation.setStation(station);

        return entityManager.merge(substation);
    }

    private Station findOrPersistStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("Station cannot be null");
        }

        PowerSupplyDistrict district = findOrPersistPowerSupplyDistrict(station.getPowerSupplyDistrict());
        station.setPowerSupplyDistrict(district);

        return entityManager.merge(station);
    }

    private PowerSupplyDistrict findOrPersistPowerSupplyDistrict(PowerSupplyDistrict district) {
        if (district == null) {
            throw new IllegalArgumentException("PowerSupplyDistrict cannot be null");
        }

        PowerSupplyEnterprise enterprise = findOrPersistPowerSupplyEnterprise(district.getPowerSupplyEnterprise());
        district.setPowerSupplyEnterprise(enterprise);

        return entityManager.merge(district);
    }

    private PowerSupplyEnterprise findOrPersistPowerSupplyEnterprise(PowerSupplyEnterprise enterprise) {
        if (enterprise == null) {
            throw new IllegalArgumentException("PowerSupplyEnterprise cannot be null");
        }

        StructuralSubdivision subdivision = findOrPersistStructuralSubdivision(enterprise.getStructuralSubdivision());
        enterprise.setStructuralSubdivision(subdivision);

        return entityManager.merge(enterprise);
    }

    private StructuralSubdivision findOrPersistStructuralSubdivision(StructuralSubdivision subdivision) {
        if (subdivision == null) {
            throw new IllegalArgumentException("StructuralSubdivision cannot be null");
        }

        Region region = findOrPersistRegion(subdivision.getRegion());
        subdivision.setRegion(region);

        return entityManager.merge(subdivision);
    }

    private Region findOrPersistRegion(Region region) {
        if (region == null) {
            throw new IllegalArgumentException("Region cannot be null");
        }

        // Проверяем, существует ли Region с таким именем
        Region existingRegion = findRegionByName(region.getName());
        if (existingRegion != null) {
            return existingRegion;
        }

        return entityManager.merge(region);
    }

    private Region findRegionByName(String name) {
        try {
            return entityManager.createQuery("SELECT r FROM Region r WHERE r.name = :name", Region.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Если Region не найден
        }
    }
}

