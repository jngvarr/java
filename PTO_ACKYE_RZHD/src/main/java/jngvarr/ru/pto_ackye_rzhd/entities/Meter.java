package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "meters")
public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String meterNumber;
    @Column(nullable = false)
    private String meterModel;
    @OneToOne
    @JoinColumn(name = "metering_point_id")
    private MeteringPoint meteringPoint;
    @ManyToOne
    @JoinColumn(name = "dc_id")
    private DC dc;
}
