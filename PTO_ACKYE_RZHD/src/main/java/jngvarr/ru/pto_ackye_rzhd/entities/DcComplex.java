package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
@Entity
@Table(name = "ivkes")
public class DcComplex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate installationDate;
    @ManyToOne
    @JoinColumn(name = "substation_id", nullable = false)
    private Substation substation;
    private int busSection;
}
