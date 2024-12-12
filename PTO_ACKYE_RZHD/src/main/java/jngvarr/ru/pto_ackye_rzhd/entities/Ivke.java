package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import jngvarr.ru.pto_ackye_rzhd.entities.others.Region;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "ivke")
@RequiredArgsConstructor
public class Ivke {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "regions", nullable = false)
    private Region region;
    String eel;
    String ech;
    String echeOrEchk;
    private String station;
//    @Column(name = "substations")
    private String substation;
    private String busSection;
    private String dcPlacement;
    private String dcNumber;
    private LocalDate dcInstallationDate;
    private LocalDate dcLastConnection;
    private Long numberOfMeters;
    private String icsState;
    private String note;
    private String comment;
}
