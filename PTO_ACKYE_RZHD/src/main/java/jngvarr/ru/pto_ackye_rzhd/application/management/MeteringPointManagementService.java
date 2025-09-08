package jngvarr.ru.pto_ackye_rzhd.application.management;

import jngvarr.ru.pto_ackye_rzhd.application.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils;
import jngvarr.ru.pto_ackye_rzhd.application.util.EntityCache;
import jngvarr.ru.pto_ackye_rzhd.application.util.StringUtils;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Substation;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeteringPointService;
import jngvarr.ru.pto_ackye_rzhd.domain.services.SubstationService;
import jngvarr.ru.pto_ackye_rzhd.domain.value.EntityType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static jngvarr.ru.pto_ackye_rzhd.application.constant.ExcelConstants.*;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.DATE_FORMATTER_DDMMYYYY;

@Data
@Slf4j
@Component
public class MeteringPointManagementService {
    private final ExcelFileService excelFileService;
    private final MeterManagementService meterManagementService;
    private final EntityCache entityCache;
    private final StringUtils stringUtils;
    private final MeteringPointService meteringPointService;
    private final SubstationService substationService;

    public MeteringPoint createIIk(Row row) {
        String mapKey = stringUtils.getStringMapKey(row);

        Substation substation = (Substation) entityCache.get(EntityType.SUBSTATION).get(mapKey);

        if (substation == null) {
            substation = excelFileService.createSubstationIfNotExists(row);
            entityCache.get(EntityType.SUBSTATION).put(mapKey, substation);
        }

        Substation finalSubstation = substation;
        MeteringPoint newMeteringPoint = new MeteringPoint();

        newMeteringPoint.setSubstation(finalSubstation);
        newMeteringPoint.setId(Long.parseLong(stringUtils.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ID))));
        newMeteringPoint.setConnection(stringUtils.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_CONNECTION)));
        newMeteringPoint.setName(stringUtils.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_NAME)));
        newMeteringPoint.setMeterPlacement(stringUtils.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_PLACEMENT)));
        newMeteringPoint.setMeteringPointAddress(stringUtils.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ADDRESS)));

        String installationDateStr = stringUtils.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_INSTALLATION_DATE));
        if (installationDateStr != null && !installationDateStr.isBlank()) {
            newMeteringPoint.setInstallationDate(LocalDate.parse(installationDateStr, DATE_FORMATTER_DDMMYYYY));
        }
        return newMeteringPoint;
    }

    public void createMeteringPoint(Row otoRow, int deviceNumberColumnIndex, String[] dataParts) {
        createNewMeteringPointInDb(dataParts, otoRow);
        excelFileService.createNewMeteringPointInExcelFile(dataParts, otoRow, deviceNumberColumnIndex);
    }

    private void createNewMeteringPointInDb(String[] dataParts, Row otoRow) {
        MeteringPoint nmp = new MeteringPoint();
        String stationName = dataParts[1];
        String substationName = dataParts[2];
        String mountingMeterNumber = dataParts[3];
        String meterType = dataParts[4].toUpperCase();
        String meteringPointName = dataParts[5];
        String meteringPointAddress = dataParts[6];
        String meterPlacement = dataParts[7];
        String mountOrg = dataParts[9];
        String date = dataParts[10];
        LocalDate meteringPointMountDate = LocalDate.parse(date, DateUtils.DATE_FORMATTER_DDMMYYYY);
        Substation s = substationService.findByName(substationName, stationName).orElse(null);
        if (s == null) {
            s = excelFileService.createSubstationIfNotExists(otoRow);
        }

        nmp.setId(meteringPointService.getNextId());
        nmp.setInstallationDate(meteringPointMountDate);
        nmp.setSubstation(s);
        nmp.setName(meteringPointName);
        nmp.setMeteringPointAddress(meteringPointAddress);
        nmp.setMeterPlacement(meterPlacement);
        nmp.setMountOrganization(mountOrg);
        Meter newMeteringPointMeter = meterManagementService.getOrCreateMeter(mountingMeterNumber, meterType, dataParts[0]);

        if (!meterManagementService.isMeterInstalled(mountingMeterNumber)) {
            nmp.setMeter(newMeteringPointMeter);
        } else {
            log.warn("Данный прибор учета уже установлен на другой точке учёта");
        }

        meteringPointService.create(nmp);
    }
}
