package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "metering_points_state")
public class MeteringPointState {
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
}
