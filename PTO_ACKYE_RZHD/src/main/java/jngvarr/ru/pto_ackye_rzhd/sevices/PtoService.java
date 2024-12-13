package jngvarr.ru.pto_ackye_rzhd.sevices;

import jngvarr.ru.pto_ackye_rzhd.entities.Iik;
import jngvarr.ru.pto_ackye_rzhd.entities.IikStatusData;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtoService {
    private final IikService iikService;
    private final IikStatusDataService iikStatusDataService;
    //    private final IvkeService ivkeService;
    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String PLAN_OTO_PATH = "d:\\Downloads\\Контроль ПУ РРЭ (Задания на ОТО РРЭ)demo — копия.xlsx";
    private final long startTime = System.currentTimeMillis();

    public void processFile() {
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH))
        ) {

            fillIIKWithListData(planOTOWorkbook.getSheet("ИИК"));
//            fillIikStatusData(planOTOWorkbook.getSheet("ИИК"));
//            fillIVKEData(planOTOWorkbook.getSheet("ИВКЭ"));
            planOTOWorkbook.close();

            log.info("Data filled successfully!");

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution time: " + duration / 1000 + " seconds");
    }

    private List<Iik> fillIIKWithListData(Sheet worksheet) {
        List<Iik> iiks = new ArrayList<>();
        for (Row row : worksheet) {
            Iik newIik = new Iik();
            IikStatusData newData = new IikStatusData();
            if (row.getRowNum() == 0) {
                // Пропускаем первую строку, если это заголовок
                continue;
            }
            newData = getiikStatusData(row);
            newIik.setId(Long.parseLong(getCellStringValue(row.getCell(1))));
            newIik.setRegion(row.getCell(2).getStringCellValue());
            newIik.setEel(row.getCell(3).getStringCellValue());
            newIik.setEch(row.getCell(4).getStringCellValue());
            newIik.setEcheOrEchk(row.getCell(5).getStringCellValue());
            newIik.setStation(row.getCell(6).getStringCellValue());
            newIik.setSubstation(row.getCell(7).getStringCellValue());
            newIik.setConnection(row.getCell(8).getStringCellValue());
            newIik.setMeteringPoint(row.getCell(9).getStringCellValue());
            newIik.setMeterPlacement(row.getCell(10).getStringCellValue());
            newIik.setMeteringPointAddress(row.getCell(11).getStringCellValue());
            newIik.setMeterModel(row.getCell(12).getStringCellValue());
            newIik.setMeterNumber(getCellStringValue(row.getCell(13)));
            newIik.setDcNumber(row.getCell(14).getStringCellValue());
            try {
                newIik.setInstallationDate(LocalDate.parse(getCellStringValue(row.getCell(15)), DATE_FORMATTER_DDMMYYYY));
            } catch (RuntimeException e) {
                log.info("Iik Id {}", newIik.getId());
            }
            newIik.setIikStatusData(newData);
            iiks.add(newIik);
        }
        return iikService.createAll(iiks);
    }

    private IikStatusData getiikStatusData(Row row) {

            IikStatusData newStatusData = new IikStatusData();

            newStatusData.setId(Long.parseLong(getCellStringValue(row.getCell(1))));
            newStatusData.setCurrentStatus(getCellStringValue(row.getCell(16)));
            newStatusData.setNotOrNotNot(getCellStringValue(row.getCell(17)));
            newStatusData.setStatus(getCellStringValue(row.getCell(19)));
            newStatusData.setDispatcherTask(getCellStringValue(row.getCell(20)));
            newStatusData.setTeamReport(getCellStringValue(row.getCell(21)));

        return iikStatusDataService.createData(newStatusData);
    }



    private List<IikStatusData> fillIikStatusData(Sheet worksheet) {
        List<IikStatusData> iikStatusData = new ArrayList<>();
        for (Row row : worksheet) {
            IikStatusData newStatusData = new IikStatusData();
            if (row.getRowNum() == 0) {
                // Пропускаем первую строку, если это заголовок
                continue;
            }
            newStatusData.setCurrentStatus(getCellStringValue(row.getCell(16)));
            newStatusData.setNotOrNotNot(getCellStringValue(row.getCell(17)));
            newStatusData.setStatus(getCellStringValue(row.getCell(19)));
            newStatusData.setDispatcherTask(getCellStringValue(row.getCell(20)));
            newStatusData.setTeamReport(getCellStringValue(row.getCell(21)));
            iikStatusData.add(newStatusData);
        }
        return iikStatusDataService.createAll(iikStatusData);
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
