package jngvarr.ru.pto_ackye_rzhd.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
//@Entity
@RequiredArgsConstructor
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    OperationLogNote note;
}
