package jngvarr.ru.pto_ackye_rzhd.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "data_concentrators")
public class Dc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "substation_id")
    private Substation substation;
    private String busSection;
    @Column(nullable = false, unique = true)
    private String dcNumber;
    private String dcModel;
    private LocalDate manufactureDate;
    private LocalDate installationDate;
    @OneToMany(mappedBy = "dc", cascade = CascadeType.ALL)
    private List<Meter> meters;
}
