package dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "visits")
    public class Visit {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "date")
        private LocalDate visitDate;
        @Column(name = "service_id")
        private Long serviceId;
        @Column(name = "client_id")
        private Long clientId;
        @Column(name = "master_id")
        private Long employeeId;
}
