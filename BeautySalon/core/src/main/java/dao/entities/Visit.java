package dao.entities;

import dao.entities.people.Client;
import dao.entities.people.Employee;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class Visit {
    private Long id;
    private LocalDate visitDate;
    private LocalDateTime startTime;
    private Servize service;
    private Client client;
    private Employee master;

    public Visit(Long id,
                 LocalDate visitDate,
                 LocalDateTime startTime,
                 Servize service,
                 Client client,
                 Employee master) {
        this.id = id;
        this.visitDate = visitDate;
        this.startTime = startTime;
        this.service = service;
        this.client = client;
        this.master = master;
    }
}
