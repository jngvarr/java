package jngvarr.ru.pto_ackye_rzhd.application.management;

import jngvarr.ru.pto_ackye_rzhd.application.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils;
import jngvarr.ru.pto_ackye_rzhd.application.util.EntityCache;
import jngvarr.ru.pto_ackye_rzhd.application.util.ExcelUtil;
import jngvarr.ru.pto_ackye_rzhd.application.util.StringUtils;
import jngvarr.ru.pto_ackye_rzhd.domain.dto.MeteringPointDTO;
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
    private final MeterManagementService meterManagementService;
    private final EntityCache entityCache;
    private final ExcelUtil excelUtil;
    private final StringUtils stringUtils;
    private final MeteringPointService meteringPointService;
    private final SubstationService substationService;
    private final SubstationManagementService substationManagementService;

    public MeteringPoint createIIk(Row row) {
        String mapKey = stringUtils.getStringMapKey(row);

        Substation substation = (Substation) entityCache.get(EntityType.SUBSTATION).get(mapKey);

        if (substation == null) {
            substation = substationManagementService.createSubstationIfNotExists(excelUtil.createSubstationDtoIfNotExists(row));
            entityCache.get(EntityType.SUBSTATION).put(mapKey, substation);
        }

        Substation finalSubstation = substation;
        MeteringPoint newMeteringPoint = new MeteringPoint();

        newMeteringPoint.setSubstation(finalSubstation);
        newMeteringPoint.setId(Long.parseLong(excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ID))));
        newMeteringPoint.setConnection(excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_CONNECTION)));
        newMeteringPoint.setName(excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_NAME)));
        newMeteringPoint.setMeterPlacement(excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_PLACEMENT)));
        newMeteringPoint.setMeteringPointAddress(excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ADDRESS)));

        String installationDateStr = excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_INSTALLATION_DATE));
        if (installationDateStr != null && !installationDateStr.isBlank()) {
            newMeteringPoint.setInstallationDate(LocalDate.parse(installationDateStr, DATE_FORMATTER_DDMMYYYY));
        }
        return newMeteringPoint;
    }

    public void createMeteringPoint(Row otoRow, int deviceNumberColumnIndex, String[] dataParts) {
        createNewMeteringPointInDb(dataParts, otoRow);
        excelUtil.createNewMeteringPointInExcelFile(dataParts, otoRow, deviceNumberColumnIndex);

    }

    private void createNewMeteringPointInDb(String[] dataParts, Row otoRow) {
        MeteringPoint nmp = new MeteringPoint();
        LocalDate meteringPointMountDate = LocalDate.parse(dataParts[10], DateUtils.DATE_FORMATTER_DDMMYYYY);
        Substation s = substationService.findByName(dataParts[2], dataParts[1]).orElse(null);
        if (s == null) {
            s = substationManagementService.createSubstationIfNotExists(excelUtil.createSubstationDtoIfNotExists(otoRow));
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
}
