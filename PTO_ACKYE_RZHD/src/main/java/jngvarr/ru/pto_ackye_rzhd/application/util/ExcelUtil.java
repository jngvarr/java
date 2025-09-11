package jngvarr.ru.pto_ackye_rzhd.application.util;

import jngvarr.ru.pto_ackye_rzhd.domain.dto.MeteringPointDTO;
import jngvarr.ru.pto_ackye_rzhd.domain.dto.SubstationDTO;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.MeteringPoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static jngvarr.ru.pto_ackye_rzhd.application.constant.ExcelConstants.*;

@Slf4j
@Component
public class ExcelUtil {
    public String getCellStringValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return new SimpleDateFormat("dd.MM.yyyy").format(cell.getDateCellValue());
                    } else {
                        return new DecimalFormat("0").format(cell.getNumericCellValue());
                    }
                case FORMULA:
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    return evaluator.evaluate(cell).getStringValue();
                default:
                    return null;
            }
        }
        return null;
    }

    public SubstationDTO createSubstationDtoIfNotExists(Row row) {
        SubstationDTO newSubstation = new SubstationDTO();
        newSubstation.setRegionName(getCellStringValue(row.getCell(CELL_NUMBER_REGION_NAME)));
        newSubstation.setSubdivisionName(getCellStringValue(row.getCell(CELL_NUMBER_STRUCTURAL_SUBDIVISION_NAME)));
        newSubstation.setPowerSupplyEnterpriseName(getCellStringValue(row.getCell(CELL_NUMBER_POWER_SUPPLY_ENTERPRISE_NAME)));
        newSubstation.setDistrictName(getCellStringValue(row.getCell(CELL_NUMBER_POWER_SUPPLY_DISTRICT_NAME)));
        newSubstation.setStationName(getCellStringValue(row.getCell(CELL_NUMBER_STATION_NAME)));
        newSubstation.setSubstationName(getCellStringValue(row.getCell(CELL_NUMBER_SUBSTATION_NAME)));
        return newSubstation;
    }

    public Optional<String[]> getMeterData(String mountingMeterNumber) {

        try (Workbook meterDataWorkbook = new XSSFWorkbook(new FileInputStream(METER_DATA_FILE_PATH))) {
            Sheet dataSheet = meterDataWorkbook.getSheet("Свод");

            for (Row row : dataSheet) {
                String cellValue = getCellStringValue(row.getCell(1));
                if (mountingMeterNumber.equals(cellValue)) {
                    String[] result = {
                            getCellStringValue(row.getCell(1)),
                            getCellStringValue(row.getCell(2)),
                            getCellStringValue(row.getCell(3))
                    };
                    return Optional.of(result);
                }
            }
            log.warn("Meter number {} not found in file {}", mountingMeterNumber, METER_DATA_FILE_PATH);

        } catch (IOException ex) {
            log.error("Error processing workbook {}", METER_DATA_FILE_PATH, ex);
        }
        return Optional.empty();
    }
}
