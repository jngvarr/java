package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import jngvarr.ru.pto_ackye_rzhd.entities.others.Region;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;


@Data
@Entity
@Table(name = "iiks")
@RequiredArgsConstructor
public class Iik {
    @Id
    Long id;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "region", nullable = false)
    private String region;
    private String eel;
    private String ech;
    private String echeOrEchk;
    private String station;
//    @Column(name = "substation")
    private String substation;
    private String connection;
    private String meteringPoint;
    private String meterPlacement;
    private String meteringPointAddress;
    private String meterModel;
    private String meterNumber;
    private String dcNumber;
    private LocalDate installationDate;
    @OneToOne
//            (cascade = CascadeType.ALL)
//    @PrimaryKeyJoinColumn
    @JoinColumn(name = "iik_status_id")
//            joinColumns = @JoinColumn(name = "iik_id"),
//            inverseJoinColumns = @JoinColumn(name = "iik_status_id"))
    private IikStatusData iikStatusData;
}
