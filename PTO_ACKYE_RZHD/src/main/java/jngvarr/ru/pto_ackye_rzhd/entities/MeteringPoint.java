package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Data
@Entity
@Table(name = "metering_points")
public class MeteringPoint {
    @Id
    Long id;
    @Column(nullable = false)
    private String name;
    private String meterPlacement;
    @Column(nullable = false)
    private String meteringPointAddress;
    private LocalDate installationDate;
    private String connection;
    @ManyToOne
    @JoinColumn(name = "substation_id", nullable = false)
    private Substation substation;
    @OneToOne
//            (cascade = CascadeType.MERGE)
    @JoinColumn(name = "meter_id")
    private Meter meter;
}

