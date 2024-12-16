package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ivkes")
public class DcComplex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
