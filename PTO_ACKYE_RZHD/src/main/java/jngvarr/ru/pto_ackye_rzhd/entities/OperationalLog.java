package jngvarr.ru.pto_ackye_rzhd.entities.others;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
//@Entity
@RequiredArgsConstructor
public class OperationalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    OperationalLogNote note;
}
