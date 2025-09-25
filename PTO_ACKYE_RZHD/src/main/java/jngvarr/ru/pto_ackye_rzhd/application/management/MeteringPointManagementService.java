package jngvarr.ru.pto_ackye_rzhd.application.management;

import jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils;
import jngvarr.ru.pto_ackye_rzhd.application.util.EntityCache;
import jngvarr.ru.pto_ackye_rzhd.application.util.ExcelUtil;
import jngvarr.ru.pto_ackye_rzhd.application.util.StringUtils;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Dc;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Substation;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeteringPointService;
import jngvarr.ru.pto_ackye_rzhd.domain.services.SubstationService;
import jngvarr.ru.pto_ackye_rzhd.domain.value.EntityType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static jngvarr.ru.pto_ackye_rzhd.application.constant.ExcelConstants.*;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.DATE_FORMATTER_DDMMYYYY;

@Data
@Slf4j
@Component
public class MeteringPointManagementService {
    private final MeterManagementService meterManagementService;
    private final EntityCache entityCache;
    private final MeteringPointService meteringPointService;
    private final SubstationService substationService;
    private final SubstationManagementService substationManagementService;

    public MeteringPoint constructIIk(Row row) {
        String mapKey = StringUtils.getStringMapKey(row, 0);

        Substation substation = (Substation) entityCache.get(EntityType.SUBSTATION).get(mapKey);

        if (substation == null) {
            substation = substationManagementService.createSubstationIfNotExists(ExcelUtil.createSubstationDto(row));
            entityCache.get(EntityType.SUBSTATION).put(mapKey, substation);
        }

        MeteringPoint newMeteringPoint = new MeteringPoint();

        newMeteringPoint.setSubstation(substation);
        newMeteringPoint.setId(Long.parseLong(ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ID))));
        newMeteringPoint.setConnection(ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_CONNECTION)));
        newMeteringPoint.setName(ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_NAME)));
        newMeteringPoint.setMeterPlacement(ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_PLACEMENT)));
        newMeteringPoint.setMeteringPointAddress(ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ADDRESS)));

        String installationDateStr = ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_INSTALLATION_DATE));
        if (installationDateStr != null && !installationDateStr.isBlank()) {
            newMeteringPoint.setInstallationDate(LocalDate.parse(installationDateStr, DATE_FORMATTER_DDMMYYYY));
        }
        return newMeteringPoint;
    }

    public MeteringPoint constructIIkFromIikContent(Row row) {
        String mapKey = StringUtils.getStringMapKey(row, 2);

        Substation substation = (Substation) entityCache.get(EntityType.SUBSTATION).get(mapKey);

        if (substation == null) {
            substation = substationManagementService.createSubstationIfNotExists(ExcelUtil.createSubstationDtoFromContent(row));
            entityCache.get(EntityType.SUBSTATION).put(mapKey, substation);
        }

        MeteringPoint newMeteringPoint = new MeteringPoint();
        Sheet sheet = row.getSheet();
        newMeteringPoint.setSubstation(substation);
        newMeteringPoint.setId(Long.parseLong(ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Идентификатор ТУ",1)))));
        newMeteringPoint.setName(ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Название ТУ",1))));
        newMeteringPoint.setMeterPlacement(ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Размещение ПУ", 2))));
        newMeteringPoint.setMeteringPointAddress(ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Адрес ТУ", 2))));

        String installationDateStr = ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Дата монт.", 1)));
        if (installationDateStr != null && !installationDateStr.isBlank()) {
            newMeteringPoint.setInstallationDate(LocalDate.parse(installationDateStr, DATE_FORMATTER_DDMMYYYY));
        }
        return newMeteringPoint;
    }

    public void createMeteringPoint(Row otoRow, int deviceNumberColumnIndex, String[] dataParts) {
        createNewMeteringPointInDb(dataParts, otoRow);
        ExcelUtil.addNewMeteringPointToExcelFile(dataParts, otoRow, deviceNumberColumnIndex);
    }

    private void createNewMeteringPointInDb(String[] dataParts, Row otoRow) {
        MeteringPoint nmp = new MeteringPoint();
        LocalDate meteringPointMountDate = LocalDate.parse(dataParts[10], DateUtils.DATE_FORMATTER_DDMMYYYY);
        Substation s = substationService.findByName(dataParts[2], dataParts[1]).orElse(null);
        if (s == null) {
            s = substationManagementService.createSubstationIfNotExists(ExcelUtil.createSubstationDto(otoRow));
        }

        nmp.setId(meteringPointService.getNextId());
        nmp.setInstallationDate(meteringPointMountDate);
        nmp.setSubstation(s);
        nmp.setName(dataParts[5]);
        nmp.setMeteringPointAddress(dataParts[6]);
        nmp.setMeterPlacement(dataParts[7]);
        nmp.setMountOrganization(dataParts[9]);
        Meter newMeteringPointMeter = meterManagementService.getOrCreateMeter(dataParts[3], dataParts[4].toUpperCase(), dataParts[0]);

        if (!meterManagementService.isMeterInstalled(dataParts[1])) {
            nmp.setMeter(newMeteringPointMeter);
        } else {
            log.warn("Данный прибор учета уже установлен на другой точке учёта");
        }
        meteringPointService.create(nmp);
    }

    public void putAllMeteringPointToCache(List<MeteringPoint> dcs) {
        for (MeteringPoint mp : dcs) {
            entityCache.put(EntityType.METERING_POINT, String.valueOf(mp.getId()), mp);
        }
    }
}
