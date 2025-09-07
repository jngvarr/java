package jngvarr.ru.pto_ackye_rzhd.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "power_supply_enterprises")
public class PowerSupplyEnterprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToOne
//    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "structural_subdivision_id", nullable = false)
    private StructuralSubdivision structuralSubdivision;
}
