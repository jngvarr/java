package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "ivkes")
public class DC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String dcNumber;
    private String dcModel;
    private LocalDate manufactureDate;
    @OneToOne
    @JoinColumn(name = "ivke_id", nullable = false)
    private DcComplex dcComplex;
}
//    @OneToMany(mappedBy = "dc", cascade = CascadeType.ALL)
//    private List<Meter> dcMeters;
