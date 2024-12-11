package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@RequiredArgsConstructor
@Entity(name = "iik_statuses")
public class IikStatusData {
    @Id
    private Long id;
    private String currentStatus;
    private String notOrNotNot;
    private String status;
    private String DispatcherTask;
    private String teamReport;
}
