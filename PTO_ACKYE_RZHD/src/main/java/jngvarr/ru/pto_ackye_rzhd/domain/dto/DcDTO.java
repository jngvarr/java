package jngvarr.ru.pto_ackye_rzhd.domain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DcDTO {
    private Long id;
    private String dcNumber;
    private String dcModel;
    private Long substationId;
    private String busSection;
    private LocalDate manufactureDate;
    private LocalDate installationDate;
}
