package jngvarr.ru.pto_ackye_rzhd.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "meters")
public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String meterNumber;
    @Column(nullable = false)
    private String meterModel;
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "metering_point_id")
//    private MeteringPoint meteringPoint;
    @ManyToOne
//    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "dc_id", nullable = false)
    private Dc dc;
}
