package jngvarr.ru.pto_ackye_rzhd.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DcDTO {
    private Long id;
    private String dcNumber;
    private String dcModel;
    private Long substationId;
    private int busSection;
    private LocalDate manufactureDate;
    private LocalDate installationDate;
}
