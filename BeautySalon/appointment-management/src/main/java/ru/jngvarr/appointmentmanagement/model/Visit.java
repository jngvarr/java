package ru.jngvarr.appointmentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.jngvarr.clientmanagement.model.Client;
import java.util.Date;

@Data
@RequiredArgsConstructor
@Entity
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date visitDate;
//    @Column(name = "clients")
//    private Client client;

}
