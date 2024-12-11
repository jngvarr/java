package jngvarr.ru.pto_ackye_rzhd.entities.others;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Meter {
    @Id
    private Long id;
}
