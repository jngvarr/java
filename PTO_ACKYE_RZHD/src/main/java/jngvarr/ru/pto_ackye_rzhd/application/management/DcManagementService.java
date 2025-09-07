package jngvarr.ru.pto_ackye_rzhd.application.management;

import jngvarr.ru.pto_ackye_rzhd.application.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.application.util.EntityCache;
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

import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.DATE_FORMATTER_DDMMYYYY;





@Component
@Slf4j
@Data
public class DcManagementService {
    private final EntityCache entityCache;
    private final DcService dcService;
    private final ExcelFileService excelFileService;

    public void createDc(Substation substation, String dcNumber, Row row) {
        Dc newDc = new Dc();
        newDc.setSubstation(substation);
        log.info("{}", row.getRowNum());
        String sectionNumber = excelFileService.getCellStringValue(row.getCell(CELL_NUMBER_BUS_SECTION_NUM));
        if (!sectionNumber.isBlank()) {
            newDc.setBusSection(Integer.parseInt(sectionNumber) == 2 ? 2 : 1);
        } else newDc.setBusSection(1);

        String installationDateStr = excelFileService.getCellStringValue(row.getCell(CELL_NUMBER_DC_INSTALLATION_DATE));
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

    public void changeMeterOnDc(Meter oldMeter, Meter newMeter) {
        Dc dc = dcService.getDcByNumber(oldMeter.getDc().getDcNumber());
        List<Meter> meters = dc.getMeters();
        meters.removeIf(m -> m.getMeterNumber().equals(oldMeter.getMeterNumber()));
        meters.add(newMeter);
        newMeter.setDc(dc);
        dc.setMeters(meters);
        dcService.updateDc(dc, dc.getId());
        oldMeter.setDc(dcService.getDcByNumber("LJ03514666"));
        meterService.updateMeter(oldMeter, oldMeter.getId());
    }
}
