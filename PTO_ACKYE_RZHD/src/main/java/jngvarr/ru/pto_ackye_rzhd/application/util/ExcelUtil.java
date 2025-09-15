package jngvarr.ru.pto_ackye_rzhd.application.util;

import jngvarr.ru.pto_ackye_rzhd.domain.dto.SubstationDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static jngvarr.ru.pto_ackye_rzhd.application.constant.ExcelConstants.*;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.STRAIGHT_FORMATTED_CURRENT_DATE;

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

    public void meterChangeInExcelFile(Row otoRow, int deviceNumberColumnIndex, String mountingMeterNumber) {
        Object mountingDeviceNumber = parseMeterNumber(mountingMeterNumber);
        // Внесение номера Устройства в журнал "Контроль ПУ РРЭ"
        if (mountingDeviceNumber instanceof Long) {
            otoRow.getCell(deviceNumberColumnIndex).setCellValue((Long) mountingDeviceNumber);
        } else {
            otoRow.getCell(deviceNumberColumnIndex).setCellValue((String) mountingDeviceNumber);
        }
    }

    // Метод для проверки и преобразования номера счетчика
    public Object parseMeterNumber(String meterNumberStr) {
        try {
            return Long.parseLong(meterNumberStr);
        } catch (NumberFormatException e) {
            return meterNumberStr;
        }
    }

    public void addNewMeteringPointInExcelFile(String[] dataParts, Row otoRow, int deviceNumberColumnIndex) {
        String deviceNumber = dataParts[0];
        String mountingMeterNumber = dataParts[3];
        String meterType = dataParts[4].toUpperCase();
        String meteringPointName = dataParts[5];
        String meteringPointAddress = dataParts[6];
        String meterPlacement = dataParts[7];
        String mountOrg = dataParts[9];
        String date = dataParts[10];
        Object mountingDeviceNumber = parseMeterNumber(mountingMeterNumber); //Номер счетчика
        if (mountingDeviceNumber instanceof Long) {
            otoRow.getCell(13).setCellValue((Long) mountingDeviceNumber);
        } else {
            otoRow.getCell(13).setCellValue((String) mountingDeviceNumber);
        }
        otoRow.getCell(9).setCellValue(meteringPointName); //Наименование точки учёта
        otoRow.getCell(10).setCellValue(meterPlacement); // Место установки счетчика (Размещение счетчика)
        otoRow.getCell(11).setCellValue(meteringPointAddress); // Адрес установки
        otoRow.getCell(12).setCellValue(meterType); // Марка счётчика
//        Object mountDeviceNumber = parseMeterNumber(mountingMeterNumber);
//        if (mountDeviceNumber instanceof Long) {
//            otoRow.getCell(deviceNumberColumnIndex).setCellValue((Long) mountDeviceNumber);
//        } else {
//            otoRow.getCell(deviceNumberColumnIndex).setCellValue((String) mountDeviceNumber);
//        }

        otoRow.getCell(14).setCellValue(deviceNumber); // Номер УСПД
        Cell mountDateCell = otoRow.getCell(15);
        mountDateCell.setCellValue(date); // Дата монтажа ТУ
        setDateCellStyle(mountDateCell);
        otoRow.getCell(16).setCellValue("НОТ"); // Текущее состояние
    }

    /**
     * Метод преобразования значения ячейки в дату, так чтоб Excel понимал его именно как дату.
     *
     * @param date Cell, ячейка содержащая значеие даты.
     */
    public void setDateCellStyle(Cell date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        CellStyle dateStyle = createDateCellStyle(date.getRow().getSheet().getWorkbook(), "dd.MM.yy", "Arial");
        try {
            date.setCellValue(sdf.parse(STRAIGHT_FORMATTED_CURRENT_DATE));
        } catch (ParseException e) {
            date.setCellStyle(dateStyle);
        }
        date.setCellStyle(dateStyle);
    }

    public CellStyle createDateCellStyle(Workbook resultWorkbook, String format, String font) {
        CellStyle dateCellStyle = resultWorkbook.createCellStyle();
        DataFormat dateFormat = resultWorkbook.createDataFormat(); // Формат даты
        dateCellStyle.setDataFormat(dateFormat.getFormat(format));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setFont(createCellFontStyle(resultWorkbook, font, (short) 10, false));
        return dateCellStyle;
    }

    public Font createCellFontStyle(Workbook workbook, String fontName, short fontSize, boolean isBold) {
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        font.setBold(isBold);
        return font;
    }
    public CellStyle createCommonCellStyle(Workbook resultWorkbook) {
        CellStyle simpleCellStyle = resultWorkbook.createCellStyle();
        Font font = createCellFontStyle(resultWorkbook, "Arial", (short) 10, false);

        simpleCellStyle.setBorderBottom(BorderStyle.THIN);
        simpleCellStyle.setBorderLeft(BorderStyle.THIN);
        simpleCellStyle.setBorderRight(BorderStyle.THIN);
        simpleCellStyle.setBorderTop(BorderStyle.THIN);
        simpleCellStyle.setFont(font);
        return simpleCellStyle;
    }
}
