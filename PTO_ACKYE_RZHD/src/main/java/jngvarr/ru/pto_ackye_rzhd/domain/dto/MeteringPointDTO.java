package jngvarr.ru.pto_ackye_rzhd.domain.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class MeteringPointDTO {

    private Long id;
    private String name;
    private String meterPlacement;
    private String meteringPointAddress;
    private LocalDate installationDate;
    private String connection;
    private String substation;
    private MeterDTO meter;
}
