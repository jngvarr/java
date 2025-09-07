package jngvarr.ru.pto_ackye_rzhd.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "data_concentrators_state")
public class dcState {
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
    private Dc dcComplex;
}


