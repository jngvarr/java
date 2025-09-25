package jngvarr.ru.pto_ackye_rzhd.application.management;

import jngvarr.ru.pto_ackye_rzhd.application.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.application.util.EntityCache;
import jngvarr.ru.pto_ackye_rzhd.application.util.ExcelUtil;
import jngvarr.ru.pto_ackye_rzhd.application.util.StringUtils;
import jngvarr.ru.pto_ackye_rzhd.domain.dto.MeterDTO;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Dc;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.domain.services.DcService;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeterService;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeteringPointService;
import jngvarr.ru.pto_ackye_rzhd.domain.value.EntityType;
import lombok.Data;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static jngvarr.ru.pto_ackye_rzhd.application.constant.ExcelConstants.*;

@Data
@Component
public class MeterManagementService {
    private final EntityCache entityCache;
    private final DcService dcService;
    private final MeterService meterService;
    private final MeteringPointService meteringPointService;

    @Getter
    private static List<String> meterTypes = Arrays.asList(
            "EM-1021", "EM-1023", "EM-2023", "KNUM-1021", "KNUM-1023", "KNUM-2023");

    boolean isMeterInstalled(String meterNum) {
        return entityCache
                .get(EntityType.METERING_POINT)
                .values()
                .stream()
                .map(o -> (MeteringPoint) o)
                .filter(Objects::nonNull)
                .anyMatch(m -> meterNum.equals(m.getMeter().getMeterNumber()));
    }

    public void changeMeter(String deviceNumber, Row otoRow, int deviceNumberColumnIndex, String[] dataParts) {
        ExcelUtil.meterChangeInExcelFile(otoRow, deviceNumberColumnIndex, dataParts[2]);
        meterChangeInDb(deviceNumber, dataParts[2]);
    }

    public Meter constructMeter(Row row) {
        String meterNumber = ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_METER_NUMBER));
        String meterModel = ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_METER_MODEL));
        String dcNum = ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_DC_NUMBER));
        return constructMeter(meterNumber, meterModel, dcNum);
    }

    public Meter constructMeterFromIikContent(Row row) {
        Sheet sheet = row.getSheet();
        String meterNumber = ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Сер. номер ПУ", 1)));
        String meterModel = ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Модель ПУ", 1)));
        String dcNum =  ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Сер. ном. УСПД", 1)));
        return constructMeter(meterNumber, meterModel, dcNum);
    }

    public Meter constructMeter(String meterNum, String meterModel, String dcNum) {
        // Dc ищем либо в карте, либо в сервисе
        Dc foundDc = Optional.ofNullable((Dc) entityCache.get(EntityType.DC).get(dcNum))
                .orElseGet(() -> dcService.getDcByNumber(dcNum));

        return Optional.ofNullable(meterNum)
                .filter(num -> !num.isBlank())                // проверка, что номер не пустой
                .filter(num -> meterModel != null && !meterModel.isBlank()) // проверка модели
                .map(num -> {
                    Meter meter = new Meter();
                    meter.setMeterNumber(num);
                    meter.setMeterModel(meterModel);
                    meter.setDc(foundDc);
                    return meter;
                })
                .orElse(null); // если номер или модель не прошли проверки — null
    }

    public Meter getOrCreateMeter(String mountingMeterNumber, String meterType, String dcNum) {

        return Optional.ofNullable(
                        meterService.getMeterByNumber(mountingMeterNumber)
                )
                .orElseGet(() -> {
                    Meter created = constructMeter(mountingMeterNumber, meterType, dcNum);
                    return meterService.create(created);
                });
    }

    private void meterChangeInDb(String deviceNumber, String mountingMeterNumber) {
        Meter m = meterService.getMeterByNumber(deviceNumber);
        Meter nm = meterService.getMeterByNumber(mountingMeterNumber);
        if (nm == null) {
            String[] nmData = ExcelUtil.getMeterData(mountingMeterNumber).orElseThrow(() ->
                    new IllegalArgumentException("Не найдены данные по " + mountingMeterNumber));
            nm = constructMeter(nmData[0], nmData[2], m.getDc().getDcNumber());
            meterService.create(nm);
        }
        MeteringPoint mp = meteringPointService.getIikByMeterId(m.getId());
        mp.setMeter(nm);
        meteringPointService.update(mp, mp.getId());
        changeMeterOnDc(m, nm);
    }

    public void changeMeterOnDc(Meter oldMeter, Meter newMeter) {
        Dc dc = dcService.getDcByNumber(oldMeter.getDc().getDcNumber());
        List<Meter> meters = dc.getMeters();
        meters.removeIf(m -> m.getMeterNumber().equals(oldMeter.getMeterNumber()));
        meters.add(newMeter);
        newMeter.setDc(dc);
        dc.setMeters(meters);
        dcService.updateDc(dc, dc.getId());
        oldMeter.setDc(dcService.getDcByNumber("LJ03514666")); //TODO Конц из отстойника, потом переделать
        meterService.updateMeter(oldMeter, oldMeter.getId());
    }

    public void putAllMeterToCache(List<Meter> all) {
        for (Meter m : all) {
            entityCache.put(EntityType.METER, m.getMeterNumber(), m);
        }
    }
}
