package jngvarr.ru.pto_ackye_rzhd.mappers;

import jngvarr.ru.pto_ackye_rzhd.dto.DcDTO;
import jngvarr.ru.pto_ackye_rzhd.dto.MeterDTO;
import jngvarr.ru.pto_ackye_rzhd.dto.MeteringPointDTO;
import jngvarr.ru.pto_ackye_rzhd.entities.Dc;
import jngvarr.ru.pto_ackye_rzhd.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.entities.Substation;

public class MeterMapper {


    public static MeterDTO toMeterDTO(Meter meter) {
        return new MeterDTO() {{
            setId(meter.getId());
            setMeterNumber(meter.getMeterNumber());
            setMeterModel(meter.getMeterModel());
            setDcNum(toDcDTO(meter.getDc()).getDcNumber());
        }};
    }

    public static Meter fromMeterDTO(MeterDTO meterDTO) {
        Meter meter = new Meter();
        meter.setMeterNumber(meterDTO.getMeterNumber());
        meter.setMeterModel(meterDTO.getMeterModel());
        return meter;
    }


    public static MeteringPointDTO toMeteringPointDTO(MeteringPoint meteringPoint) {
        MeteringPointDTO dto = new MeteringPointDTO();
        dto.setId(meteringPoint.getId());
        dto.setName(meteringPoint.getName());
        dto.setMeteringPointAddress(meteringPoint.getMeteringPointAddress());
        dto.setInstallationDate(meteringPoint.getInstallationDate());
        dto.setSubstation(meteringPoint.getSubstation().getName());
        dto.setMeter(MeterMapper.toMeterDTO(meteringPoint.getMeter()));
        return dto;
    }

    public static MeteringPoint fromMeteringPointDTO(MeteringPointDTO iik) {
        MeteringPoint meteringPoint = new MeteringPoint();
        meteringPoint.setName(iik.getName());
        meteringPoint.setMeterPlacement(iik.getMeterPlacement());
        meteringPoint.setConnection(iik.getConnection());
        meteringPoint.setMeteringPointAddress(iik.getMeteringPointAddress());
        meteringPoint.setSubstation(new Substation() {{
            setName(iik.getSubstation());
        }});
        return meteringPoint;
    }

    public static DcDTO toDcDTO(Dc dc) {
        return new DcDTO() {
            {
                setId(dc.getId());
                setDcNumber(dc.getDcNumber());
                setDcModel(dc.getDcModel());
                setSubstationId(dc.getSubstation().getId());
                setBusSection((dc.getBusSection()));
                setInstallationDate(dc.getInstallationDate());
                setManufactureDate(dc.getManufactureDate());
            }
        };
    }

    public static Dc fromDcDTO(DcDTO dcDTO) {
        Dc dc = new Dc();
        dc.setId(dcDTO.getId());
        dc.setDcNumber(dcDTO.getDcNumber());
        dc.setDcModel(dcDTO.getDcModel());
        dc.setInstallationDate(dcDTO.getInstallationDate());
        dc.setManufactureDate(dcDTO.getManufactureDate());
        dc.setBusSection(dcDTO.getBusSection());
        return dc;
    }
}
