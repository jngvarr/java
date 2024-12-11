package jngvarr.ru.pto_ackye_rzhd.sevices;

import jngvarr.ru.pto_ackye_rzhd.entities.Iik;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtoService {
    private final IikService iikService;
    private final IvkeService ivkeService;

    private static final String PLAN_OTO_PATH = "d:\\Downloads\\Контроль ПУ РРЭ (Задания на ОТО РРЭ)demo.xlsx";
    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String FOLDER_PATH = "d:\\Downloads\\пто\\reports\\" + LocalDate.now().format(DATE_FORMATTER_DDMMYYYY);
    private final long startTime = System.currentTimeMillis();

    private void processFile() {
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             FileOutputStream fileOut = new FileOutputStream(PLAN_OTO_PATH)) {

            fillIIKData(planOTOWorkbook.getSheet("ИИК"));
            fillIVKEData(planOTOWorkbook.getSheet("ИВКЭ"));
//            planOTOWorkbook.write(fileOut);

            log.info("Data filled successfully!");

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution time: " + duration / 1000 + " seconds");
    }

    private void fillIIKData(Sheet worksheet) {
        Row firstRow = worksheet.getRow(0);
        int lastColumnNum = firstRow.getLastCellNum();
        int enabledCount = 0;

        for (Row row : worksheet) {
            Iik newIik = new Iik();
            newIik.setId(Long.parseLong(row.getCell(0).getStringCellValue()));
            newIik.setRegion(row.getCell(1).getStringCellValue());
            newIik.setEel(row.getCell(2).getStringCellValue());
            newIik.setEch(row.getCell(3).getStringCellValue());
            newIik.setEcheOrEchk(row.getCell(4).getStringCellValue());
            newIik.setSubstation(row.getCell(5).getStringCellValue());
            newIik.setConnection(row.getCell(6).getStringCellValue());
            newIik.setMeteringPoint(row.getCell(7).getStringCellValue());
            newIik.setMeterPlacement(row.getCell(8).getStringCellValue());
            newIik.setMeteringPointAddress(row.getCell(9).getStringCellValue());
            newIik.setMeterModel(row.getCell(10).getStringCellValue());
            newIik.setMeterNumber(Integer.parseInt(row.getCell(11).getStringCellValue()));
            newIik.setDcNumber(Integer.parseInt(row.getCell(12).getStringCellValue()));
            newIik.setInstallationDate(row.getCell(13).getDateCellValue().);
            iikService.createIik(newIik);

        }
    }

    Cell counterCell = row.getCell(13);
    String counterNumber = getCellStringValue(counterCell);
        if(counterNumber !=null)

    {
        String key = counterNumber.trim();
        if (dataMaps.get(DataType.DATA_CONTROL).containsKey(key)) {
            Cell cell = row.createCell(lastColumnNum);
            String profile = dataMaps.get(DataType.DATA_CONTROL).get(key);
            if ("Достоверные".equals(profile)) enabledCount++;
            cell.setCellValue(profile);
        }
        if (dataMaps.get(DataType.NORMALLY_TURNED_OFF).containsKey(key)) {
            Cell cell = row.createCell(17);
            cell.setCellValue(dataMaps.get(DataType.NORMALLY_TURNED_OFF).get(key));
        }
        if (dataMaps.get(DataType.IIK_STATUS).containsKey(key)) {
            Cell cell = row.createCell(19);
            cell.setCellValue(dataMaps.get(DataType.IIK_STATUS).get(key));
        }
    }


    setTopRowProperties(firstRow, lastColumnNum, enabledCount);
}


private void fillIVKEData(Sheet ivkeSheet) {
    DataFormat poiDataFormat = ivkeSheet.getWorkbook().createDataFormat();
    CellStyle dateCellStyle = ivkeSheet.getWorkbook().createCellStyle();
    dateCellStyle.setDataFormat(poiDataFormat.getFormat("dd.MM.yyyy"));

    for (Row row : ivkeSheet) {
        Cell ivkeCell = row.getCell(9);
        String ivkeNumber = getCellStringValue(ivkeCell);
        if (ivkeNumber != null) {
            String key = ivkeNumber.trim();
            if (dataMaps.get(DataType.CONNECTION_DIAG).containsKey(key)) {
                Cell connectionDiagCol = row.createCell(11);
                String dateString = dataMaps.get(DataType.CONNECTION_DIAG).get(key);

                // Попытка преобразовать строку в дату
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    connectionDiagCol.setCellValue(sdf.parse(dateString));
                    connectionDiagCol.setCellStyle(dateCellStyle); // Устанавливаем стиль даты
                } catch (ParseException e) {
                    // Если дата не распознана, сохраняем как текст
                    connectionDiagCol.setCellValue(dateString);
                }
            }
        }
    }
}

private static String getCellStringValue(Cell cell) {
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
}
