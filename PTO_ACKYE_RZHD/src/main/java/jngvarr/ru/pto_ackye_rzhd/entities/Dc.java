package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
@Table(name = "data_concentrators")
public class Dc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "substation_id", nullable = false)
    private Substation substation;
    private int busSection;
    @Column(nullable = false)
    private String dcNumber;
    private String dcModel;
    private LocalDate manufactureDate;
    private LocalDate installationDate;
}
