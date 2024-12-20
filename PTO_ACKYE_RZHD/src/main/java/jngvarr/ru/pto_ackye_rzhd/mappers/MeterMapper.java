package jngvarr.ru.pto_ackye_rzhd.mappers;

public class MeterMapper {
    public static MeterSummaryDTO toSummaryDTO(Meter meter) {
        MeterSummaryDTO dto = new MeterSummaryDTO();
        dto.setId(meter.getId());
        dto.setMeterNumber(meter.getMeterNumber());
        dto.setMeterModel(meter.getMeterModel());
        dto.setDcNumber(meter.getDc().getDcNumber());
        return dto;
    }

    public static MeterDetailsDTO toDetailsDTO(Meter meter) {
        MeterDetailsDTO dto = new MeterDetailsDTO();
        dto.setId(meter.getId());
        dto.setMeterNumber(meter.getMeterNumber());
        dto.setMeterModel(meter.getMeterModel());
        dto.setMeteringPoint(toMeteringPointDTO(meter.getMeteringPoint()));
        dto.setDc(toDcDTO(meter.getDc()));
        return dto;
    }

    public static MeteringPointDTO toMeteringPointDTO(MeteringPoint meteringPoint) {
        MeteringPointDTO dto = new MeteringPointDTO();
        dto.setId(meteringPoint.getId());
        dto.setName(meteringPoint.getName());
        dto.setMeteringPointAddress(meteringPoint.getMeteringPointAddress());
        dto.setInstallationDate(meteringPoint.getInstallationDate());
        dto.setSubstationName(meteringPoint.getSubstation().getName());
        return dto;
    }

    public static DcDTO toDcDTO(Dc dc) {
        DcDTO dto = new DcDTO();
        dto.setId(dc.getId());
        dto.setDcNumber(dc.getDcNumber());
        dto.setDcModel(dc.getDcModel());
        return dto;
    }
}
