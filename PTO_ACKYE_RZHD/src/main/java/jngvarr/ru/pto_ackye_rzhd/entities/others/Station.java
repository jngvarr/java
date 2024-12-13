package jngvarr.ru.pto_ackye_rzhd.entities.others;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "stations")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "power_supply_district_id", nullable = false)
    private PowerSupplyDistrict powerSupplyDistrict;
}
