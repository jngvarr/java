package jngvarr.ru.pto_ackye_rzhd.entities.others;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sustations")
public class Substation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;
    private String connection;
}
