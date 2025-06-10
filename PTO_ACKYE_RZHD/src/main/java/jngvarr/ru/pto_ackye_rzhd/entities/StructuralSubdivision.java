package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "structural_subdivisions")
public class StructuralSubdivision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToOne
//    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;
}
