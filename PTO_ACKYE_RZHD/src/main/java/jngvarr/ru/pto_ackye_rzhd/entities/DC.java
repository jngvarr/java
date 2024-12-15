package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "dcs")
public class DC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String dcNumber;
    private String dcModel;
    private int meterQuantity;
    private LocalDate manufactureDate;
    @ManyToOne
    @JoinColumn(name = "substation_id", nullable = false)
    private Substation substation;
    private String busSection;
    @OneToMany(mappedBy = "dc", cascade = CascadeType.ALL)
    private List<Meter> dcMeters;
}
