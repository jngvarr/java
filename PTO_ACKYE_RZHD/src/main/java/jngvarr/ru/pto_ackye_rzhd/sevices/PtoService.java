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
import java.time.ZoneId;
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
//    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
//    private static final String FOLDER_PATH = "d:\\Downloads\\пто\\reports\\" + LocalDate.now().format(DATE_FORMATTER_DDMMYYYY);
    private final long startTime = System.currentTimeMillis();

    private void processFile() {
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             FileOutputStream fileOut = new FileOutputStream(PLAN_OTO_PATH)) {

            fillIIKData(planOTOWorkbook.getSheet("ИИК"));
//            fillIVKEData(planOTOWorkbook.getSheet("ИВКЭ"));
//            planOTOWorkbook.write(fileOut);

            log.info("Data filled successfully!");

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution time: " + duration / 1000 + " seconds");
    }

    private Iik fillIIKData(Sheet worksheet) {
        for (Row row : worksheet) {
            if (row.getRowNum() == 0) {
                // Пропускаем первую строку, если это заголовок
                continue;
            }
            return iikService.createIik(new Iik() {
                {
                    setId(Long.parseLong(row.getCell(1).getStringCellValue()));
                    setRegion(row.getCell(2).getStringCellValue());
                    setEel(row.getCell(3).getStringCellValue());
                    setEch(row.getCell(4).getStringCellValue());
                    setEcheOrEchk(row.getCell(5).getStringCellValue());
//                    setSubstation(row.getCell(6).getStringCellValue());
//                    setConnection(row.getCell(7).getStringCellValue());
//                    setMeteringPoint(row.getCell(8).getStringCellValue());
//                    setMeterPlacement(row.getCell(9).getStringCellValue());
//                    setMeteringPointAddress(row.getCell(10).getStringCellValue());
//                    setMeterModel(row.getCell(11).getStringCellValue());
//                    setMeterNumber(Integer.parseInt(row.getCell(12).getStringCellValue()));
//                    setDcNumber(Integer.parseInt(row.getCell(13).getStringCellValue()));
//                    setInstallationDate(row.getCell(14).getDateCellValue()
//                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }
            });
        }
        return null;
    }


//    private void fillIVKEData(Sheet ivkeSheet) {
//        DataFormat poiDataFormat = ivkeSheet.getWorkbook().createDataFormat();
//        CellStyle dateCellStyle = ivkeSheet.getWorkbook().createCellStyle();
//        dateCellStyle.setDataFormat(poiDataFormat.getFormat("dd.MM.yyyy"));
//
//        for (Row row : ivkeSheet) {
//            Cell ivkeCell = row.getCell(9);
//            String ivkeNumber = getCellStringValue(ivkeCell);
//            if (ivkeNumber != null) {
//                String key = ivkeNumber.trim();
//                if (dataMaps.get(DataType.CONNECTION_DIAG).containsKey(key)) {
//                    Cell connectionDiagCol = row.createCell(11);
//                    String dateString = dataMaps.get(DataType.CONNECTION_DIAG).get(key);
//
//                    // Попытка преобразовать строку в дату
//                    try {
//                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//                        connectionDiagCol.setCellValue(sdf.parse(dateString));
//                        connectionDiagCol.setCellStyle(dateCellStyle); // Устанавливаем стиль даты
//                    } catch (ParseException e) {
//                        // Если дата не распознана, сохраняем как текст
//                        connectionDiagCol.setCellValue(dateString);
//                    }
//                }
//            }
//        }
//    }

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
