package jngvarr.ru.pto_ackye_rzhd.application.management;

import jngvarr.ru.pto_ackye_rzhd.application.util.EntityCache;
import jngvarr.ru.pto_ackye_rzhd.application.util.ExcelUtil;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Dc;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Substation;
import jngvarr.ru.pto_ackye_rzhd.domain.services.DcService;
import jngvarr.ru.pto_ackye_rzhd.domain.value.EntityType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jngvarr.ru.pto_ackye_rzhd.application.constant.ExcelConstants.*;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.DATE_FORMATTER_DDMMYYYY;
@Component
@Slf4j
@Data
public class DcManagementService {
    private final EntityCache entityCache;
    private final DcService dcService;
    private final ExcelUtil excelUtil;

    public void createDc(Substation substation, String dcNumber, Row row) {
        Dc newDc = new Dc();
        newDc.setSubstation(substation);
        log.info("{}", row.getRowNum());
        String sectionNumber = excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_BUS_SECTION_NUM));
        if (!sectionNumber.isBlank()
        ) {
            newDc.setBusSection(Integer.parseInt(sectionNumber) == 2 ? 2 : 1);
        } else newDc.setBusSection(1);

        String installationDateStr = excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_DC_INSTALLATION_DATE));
        if (installationDateStr != null && !installationDateStr.isBlank()) {
            newDc.setInstallationDate(LocalDate.parse(installationDateStr, DATE_FORMATTER_DDMMYYYY));
        }
        newDc.setDcModel(DC_MODEL);
        newDc.setMeters(new ArrayList<>());
        newDc.setDcNumber(dcNumber);
        dcService.createDc(newDc);
        entityCache.get(EntityType.DC).putIfAbsent(dcNumber, newDc);
    }

    public Dc createVirtualDc(Substation substation, String dcNumber) {
        Dc newDc = new Dc();
        newDc.setSubstation(substation);
        newDc.setDcModel("Virtual");
        newDc.setMeters(new ArrayList<>());
        newDc.setDcNumber(dcNumber);
        dcService.createDc(newDc);
        entityCache.get(EntityType.DC).putIfAbsent(dcNumber, newDc);
        return newDc;
    }

    public Dc addMeterToDc(Meter meter, String dcNum) {
        if (entityCache.get(EntityType.DC).containsKey(dcNum)) {
            Dc dc = dcService.getDcByNumber(dcNum);
            dc.getMeters().add(meter);
            return dc;
        }
        return null;
    }

    public void getAllDcMap(List<Dc> dcs) {
        for (Dc dc : dcs) {
            entityCache.put(EntityType.DC, dc.getDcNumber(), dc);
        }
    }

    public void saveDcFromMap() {
        for (
                Object dc : entityCache.get(EntityType.DC).values()) {
            dcService.createDc((Dc) dc);
        }
    }
}
