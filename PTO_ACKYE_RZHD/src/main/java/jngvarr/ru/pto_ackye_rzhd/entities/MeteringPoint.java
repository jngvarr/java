package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Data
@Entity
@Table(name = "iiks")
public class MeteringPoint {
    @Id
    Long id;
    private String name;
    private String meterPlacement;
    private String meteringPointAddress;
    private LocalDate installationDate;
    @ManyToOne
    @JoinColumn(name = "substation_id")
    private Substation substation;
}
