package jngvarr.ru.pto_ackye_rzhd.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class MeterDTO {
    private Long id;
    private String meterNumber;
    private String meterModel;
    private Long meteringPointId;
    private String dcNum;
    
}
