package ru.jngvarr.appointmentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "visits")
    public class VisitData {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "date")
        private LocalDate visitDate;
        @Column(name = "start_time")
        private LocalDateTime startTime;
        @Column(name = "service_id")
        private Long serviceId;
        @Column(name = "client_id")
        private Long clientId;
        @Column(name = "master_id")
        private Long employeeId;
}
