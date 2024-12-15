package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "iik_statuses")
public class IikStatusData {
    @Id
    private Long id;
    @Column(nullable = false)
    private LocalDate lastCommunicationDate;
    @Column(nullable = false)
    private String currentState;
    @Column(nullable = false)
    private String notOrNotNot;
    @Column(nullable = false)
    private String gorizontStatus;
    private String DispatcherTask;
    private String teamReport;
    @OneToOne
    @PrimaryKeyJoinColumn
    private MeteringPoint meteringPoint;
}
