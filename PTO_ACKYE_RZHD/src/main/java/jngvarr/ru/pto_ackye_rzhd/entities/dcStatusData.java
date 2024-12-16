package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "ivke_states")
public class dcStatusData {
    @Id
    private Long id;
    @Column(nullable = false)
    private String currentState;
    @Column(nullable = false)
    private LocalDate lastConnectionDate;
    private String DispatcherTask;
    private String teamReport;
    @OneToOne
    @PrimaryKeyJoinColumn
    private DcComplex dcComplex;
}


