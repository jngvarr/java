package jngvarr.ru.pto_ackye_rzhd.entities.others;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "meters")
public class Meter {
    @Id
    private String meterNumber;
    private String meterModel;
}
