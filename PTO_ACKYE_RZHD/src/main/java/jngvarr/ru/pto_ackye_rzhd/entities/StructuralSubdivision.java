package jngvarr.ru.pto_ackye_rzhd.entities.others;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

@Data
@Entity
@Table(name = "structural_subdivisions")
public class StructuralSubdivision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;
}
