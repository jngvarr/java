package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "power_supply_districts")
public class PowerSupplyDistrict {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToOne
//    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "power_supply_enterprise_id", nullable = false)
    private PowerSupplyEnterprise powerSupplyEnterprise;
}
