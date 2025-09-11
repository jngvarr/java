package jngvarr.ru.pto_ackye_rzhd.application.services;

import jngvarr.ru.pto_ackye_rzhd.application.management.DcManagementService;
import jngvarr.ru.pto_ackye_rzhd.application.management.MeterManagementService;
import jngvarr.ru.pto_ackye_rzhd.application.management.MeteringPointManagementService;
import jngvarr.ru.pto_ackye_rzhd.application.management.SubstationManagementService;
import jngvarr.ru.pto_ackye_rzhd.application.util.EntityCache;
import jngvarr.ru.pto_ackye_rzhd.application.util.ExcelUtil;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Dc;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Substation;
import jngvarr.ru.pto_ackye_rzhd.domain.services.DcService;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeterService;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeteringPointService;
import jngvarr.ru.pto_ackye_rzhd.domain.value.EntityType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static jngvarr.ru.pto_ackye_rzhd.application.constant.ExcelConstants.*;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.STRAIGHT_FORMATTED_CURRENT_DATE;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelFileService {

    private final DcService dcService;
    private final MeterService meterService;
    private final EntityCache entityCache;
    private final DcManagementService dcManagementService;
    private final TBotConversationStateService conversationStateService;
    private final SubstationManagementService substationManagementService;
    private final MeterManagementService meterManagementService;
    private final MeteringPointManagementService meteringPointManagementService;
    private final MeteringPointService meteringPointService;
    private final ExcelUtil excelUtil;

    public void copyRow(Row sourceRow, Row targetRow, int columnCount) {
        for (int i = 0; i <= columnCount; i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);
            Workbook operationLog = targetRow.getSheet().getWorkbook();

            CellStyle defaultCellStyle = createCommonCellStyle(operationLog);
            CellStyle dateCellStyle = createDateCellStyle(operationLog, "dd.MM.YYYY", "Calibri");

            if (sourceCell != null) {
                switch (sourceCell.getCellType()) {
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(sourceCell)) {
                            targetCell.setCellValue(sourceCell.getDateCellValue()); // Копируем дату
                            targetCell.setCellStyle(dateCellStyle); // Применяем стиль для даты
                        } else {
                            targetCell.setCellValue(sourceCell.getNumericCellValue()); // Копируем число
                            targetCell.setCellStyle(defaultCellStyle);
                        }
                        break;
                    case BOOLEAN:
                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                    case FORMULA:
                        targetCell.setCellFormula(sourceCell.getCellFormula());
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                    default:
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                }
            }
        }
    }

    CellStyle createDateCellStyle(Workbook resultWorkbook, String format, String font) {
        CellStyle dateCellStyle = resultWorkbook.createCellStyle();
        DataFormat dateFormat = resultWorkbook.createDataFormat(); // Формат даты
        dateCellStyle.setDataFormat(dateFormat.getFormat(format));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setFont(createCellFontStyle(resultWorkbook, font, (short) 10, false));
        return dateCellStyle;
    }


    CellStyle createCommonCellStyle(Workbook resultWorkbook) {
        CellStyle simpleCellStyle = resultWorkbook.createCellStyle();
        Font font = createCellFontStyle(resultWorkbook, "Arial", (short) 10, false);

        simpleCellStyle.setBorderBottom(BorderStyle.THIN);
        simpleCellStyle.setBorderLeft(BorderStyle.THIN);
        simpleCellStyle.setBorderRight(BorderStyle.THIN);
        simpleCellStyle.setBorderTop(BorderStyle.THIN);
        simpleCellStyle.setFont(font);
        return simpleCellStyle;
    }

    private Font createCellFontStyle(Workbook workbook, String fontName, short fontSize, boolean isBold) {
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        font.setBold(isBold);
        return font;
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

    public void clearCellData(int[] ints, Row row) {
        for (int anInt : ints) {
            row.createCell(anInt).setCellValue("");
        }
    }

    public int findColumnIndex(Sheet sheet, String columnName) {
        Row headerRow = sheet.getRow(0); // Заголовок на первой строке
        if (headerRow == null) return -1;

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getStringCellValue().toLowerCase().startsWith(columnName.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    public Map<String, String> getPhotoSavingPathFromExcel() {

        Map<String, String> paths = null;
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH))) {
            paths = new HashMap<>();
            Sheet iikSheet = planOTOWorkbook.getSheet("ИИК");
            int meterNumberColumnIndex = findColumnIndex(iikSheet, "Номер счетчика");
            int dcNumberColumnIndex = findColumnIndex(iikSheet, "Номер УСПД");
            int eelColumnIndex = findColumnIndex(iikSheet, "ЭЭЛ");
            int stationColumnIndex = findColumnIndex(iikSheet, "Железнодорожная станция");
            int substationColumnIndex = findColumnIndex(iikSheet, "ТП/КТП");
            int meterPointIndex = findColumnIndex(iikSheet, "Точка учёта");
            for (Row row : iikSheet) {
                String meterNum = excelUtil.getCellStringValue(row.getCell(meterNumberColumnIndex));
                String dcNum = excelUtil.getCellStringValue(row.getCell(dcNumberColumnIndex));
                if (meterNum != null) {
                    paths.put(meterNum,
                            EEL_TO_NTEL.get(row.getCell(eelColumnIndex).getStringCellValue()) + "\\" +
                                    row.getCell(stationColumnIndex).getStringCellValue() + "\\" +
                                    row.getCell(substationColumnIndex).getStringCellValue() + "\\" +
                                    row.getCell(meterPointIndex).getStringCellValue());
                }
                if (dcNum != null) {
                    paths.putIfAbsent(dcNum,
                            EEL_TO_NTEL.get(row.getCell(eelColumnIndex).getStringCellValue()) + "\\" +
                                    row.getCell(stationColumnIndex).getStringCellValue() + "\\" +
                                    row.getCell(substationColumnIndex).getStringCellValue() + "\\");
                }
            }
        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
        return paths;
    }

    // Метод для проверки и преобразования номера счетчика
    public Object parseMeterNumber(String meterNumberStr) {
        try {
            return Long.parseLong(meterNumberStr);
        } catch (NumberFormatException e) {
            return meterNumberStr;
        }
    }

    private void fillDcSection(Sheet dcWorkSheet, String taskOrder, boolean isDcChange) { // заполнение данных на вкладке "ИВКЭ"
        int dcNumberColIndex = findColumnIndex(dcWorkSheet, "Серийный номер концентратора");
        int dcCurrentStateColIndex = findColumnIndex(dcWorkSheet, "Состояние ИВКЭ");

        for (Row row : dcWorkSheet) {
            String deviceNumber = excelUtil.getCellStringValue(row.getCell(dcNumberColIndex));
            String logData = conversationStateService.getOtoLog().getOrDefault(deviceNumber, "");
            if (!logData.isEmpty()) {
                row.createCell(dcCurrentStateColIndex).setCellValue(taskOrder);
                if (isDcChange) row.getCell(dcNumberColIndex).setCellValue(logData.split("_")[1]);
            }
        }
    }

    public void sheetsFilling(long userId) {
        if (conversationStateService.getOtoLog().isEmpty()) return;
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             Workbook operationLog = new XSSFWorkbook(new FileInputStream(OPERATION_LOG_PATH));
             FileOutputStream fileOut = new FileOutputStream(OPERATION_LOG_PATH);
             FileOutputStream fileOtoOut = new FileOutputStream(PLAN_OTO_PATH);
        ) {

            boolean isDcWorks = conversationStateService.isOtoLogContainsDcWorks();
            boolean isDcChange = conversationStateService.isOtoLogContainsDcChange();

            Sheet meterWorkSheet = planOTOWorkbook.getSheet("ИИК");
            Sheet operationLogSheet = operationLog.getSheet("ОЖ");

            String taskOrder = dataPreparing(operationLogSheet, meterWorkSheet, isDcWorks);

            if (isDcWorks) {
                fillDcSection(planOTOWorkbook.getSheet("ИВКЭ"), taskOrder, isDcChange);
            }
            operationLog.write(fileOut);
            planOTOWorkbook.write(fileOtoOut);
            conversationStateService.clearOtoLog();

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
    }

    public void copyAndFillLogRow(Row otoRow, Row newLogRow, int orderColumnNumber, String taskOrder, List<String> columns) {
        copyRow(otoRow, newLogRow, orderColumnNumber);
        Cell date = newLogRow.getCell(16);
        setDateCellStyle(date);
        newLogRow.getCell(17).setCellValue(columns.get(0));
        newLogRow.getCell(18).setCellValue(columns.get(1));
        newLogRow.createCell(19);
//        newLogRow.getCell(19).setCellValue("");
        newLogRow.getCell(20).setCellValue("Исполнитель"); //TODO: взять исполнителя из БД по userId
        newLogRow.getCell(21).setCellValue(taskOrder);
        otoRow.getCell(orderColumnNumber).setCellValue(taskOrder);
//      newLogRow.createCell(22).setCellValue("Выполнено");   //TODO: добавить после реализации внесения корректировок в Горизонт либо БД
    }

    private String dataPreparing(Sheet operationLogSheet, Sheet meterSheet, boolean isDcWorks) {
        int orderColumnNumber = findColumnIndex(meterSheet, "Отчет бригады о выполнении ОТО");
        int deviceNumberColumnIndex = findColumnIndex(meterSheet, isDcWorks ? "Номер УСПД" : "Номер счетчика");
        int operationLogLastRowNumber = operationLogSheet.getLastRowNum();
        int addedRows = 0;
        boolean isLogFilled = false;
        boolean isMounting = conversationStateService.isOtoLogContainsMountWork();
        String taskOrder = "";

        List<Row> meterRows = new ArrayList<>();
        for (Row row : meterSheet) {
            meterRows.add(row);
        }

        for (Row otoRow : meterRows) {
            String deviceNumber = excelUtil.getCellStringValue(otoRow.getCell(deviceNumberColumnIndex));
            String logData = conversationStateService.getOtoLog().getOrDefault(deviceNumber, "");
            boolean dataContainsNot123 = logData.contains("НОТ1") || logData.contains("НОТ2") || logData.contains("НОТ3");
            boolean dataContainsNotNot5 = logData.contains("НОТ") || logData.contains("НОТ5");

            if (!logData.isEmpty()) {
                if (!isLogFilled) {
                    Row newLogSheetRow = operationLogSheet.createRow(operationLogLastRowNumber + ++addedRows);
                    if (isDcWorks) {
                        clearCellData(getIndexesOfCleaningCells(DC_COLUMNS_TO_CLEAR, meterSheet), newLogSheetRow); //удаление данных из ненужных ячеек
                    }
                    if (isMounting) {
                        int meterSheetLastRowNumber = meterSheet.getLastRowNum();
                        Row newOtoRow = meterSheet.createRow(meterSheetLastRowNumber + 1);
                        copyRow(otoRow, newOtoRow, orderColumnNumber);
                        clearCellData(getIndexesOfCleaningCells(METER_MOUNT_COLUMNS_TO_CLEAR, meterSheet), newOtoRow);
                        taskOrder = addOtoData(deviceNumber, logData, newLogSheetRow, newOtoRow, deviceNumberColumnIndex, dataContainsNot123, orderColumnNumber);
                    } else {
                        copyRow(otoRow, newLogSheetRow, orderColumnNumber);
                        taskOrder = addOtoData(deviceNumber, logData, newLogSheetRow, otoRow, deviceNumberColumnIndex, dataContainsNot123, orderColumnNumber);
                    }
                    if (addedRows == conversationStateService.getOtoLog().size()) isLogFilled = true;
                }
                if (dataContainsNot123) {
                    clearCellData(getIndexesOfCleaningCells(NOT_123_COLUMNS_TO_CLEAR, meterSheet), otoRow);
                    String notType = taskOrder.substring(taskOrder.indexOf("(") + 1, taskOrder.indexOf("(") + 1 + 4);
                    otoRow.getCell(findColumnIndex(meterSheet, "Текущее состояние")).setCellValue(notType);
                }
                if (dataContainsNotNot5) {
                    String notType = taskOrder.substring(taskOrder.indexOf("НОТ"), taskOrder.indexOf("НОТ") + 3);
                    otoRow.getCell(findColumnIndex(meterSheet, "Текущее состояние")).setCellValue(notType);
                }
            }
        }
//        addedRows = 0;
        return taskOrder;
    }

    public void fillDbWithData(Sheet ivkeSheet, Sheet iikSheet) {
        fillDbWithIvkeData(ivkeSheet);
        dcManagementService.saveDcFromMap();
        fillDbWithIikData(iikSheet);
        dcManagementService.saveDcFromMap(); //TODO переделать
    }

    public void addDataFromExcelFile(String dataFilePath) {
        long startTime = System.currentTimeMillis();
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(dataFilePath))
        ) {
            fillDbWithData(planOTOWorkbook.getSheet("ИВКЭ"), planOTOWorkbook.getSheet("ИИК"));

            log.info("Data filled successfully!");

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution time: " + duration / 1000 + " seconds");
    }

    public void fillDbWithIvkeData(Sheet sheet) {
        dcManagementService.getAllDcMap(dcService.getAllDc());
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Пропускаем первую строку, это заголовок
                continue;
            }
            String dcNumber = excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_DC_NUMBER));
            if (!entityCache.get(EntityType.DC).containsKey(dcNumber)) {
                Substation substation = substationManagementService.createSubstationIfNotExists(excelUtil.createSubstationDtoIfNotExists(row));
                dcManagementService.createDc(substation, dcNumber, row);
            }
        }
    }

    public void fillDbWithIikData(Sheet sheet) {
        Map<Long, MeteringPoint> meteringPoints = new HashMap<>();
        for (MeteringPoint mp : meteringPointService.getAllIik()) {
            meteringPoints.put(mp.getId(), mp);
        }

        Map<String, Meter> meters = new HashMap<>();
        for (Meter m : meterService.getAllMeters()) {
            meters.put(m.getMeterNumber(), m);
        }

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Meter newMeter = meterManagementService.constructMeter(row);
            MeteringPoint newIik = meteringPointManagementService.createIIk(row);

            if (!meteringPoints.containsKey(newIik.getId())) {
                String dcNumber = excelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_DC_NUMBER));
                if (dcNumber == null) {
                    if (newIik.getMeteringPointAddress() == null) newIik.setMeteringPointAddress("");
                } else {
                    Dc dcByNumber = dcService.getDcByNumber(dcNumber);
                    if (dcByNumber == null && !dcNumber.isBlank()) {
                        dcByNumber = dcManagementService.createVirtualDc(newIik.getSubstation(), dcNumber);
                    }

                    if (dcByNumber != null) {
                        newMeter.setDc(dcByNumber);
                        if (!meters.containsKey(newMeter.getMeterNumber())) {
                            newMeter = meterService.create(newMeter);
                        }
//                    newMeter = meterService.getMeterByNumber(newMeter.getMeterNumber());
                        dcByNumber.getMeters().add(newMeter);
                        entityCache.get(EntityType.DC).put(dcByNumber.getDcNumber(), dcByNumber);
                    }
                }
                newIik.setMeter(newMeter);
                meteringPoints.put(newIik.getId(), newIik);
            }
        }
        for (MeteringPoint point : meteringPoints.values()) {
            meteringPointService.create(point);
        }
    }

    private String addOtoData(String deviceNumber, String logData, Row newLogRow, Row otoRow, int deviceNumberColumnIndex, boolean dataContainsNot123, int orderColumnNumber) {
        String workType = logData.substring(0, logData.indexOf("_"));
        String[] dataParts = logData.split("_");
        List<String> otoWorksStrings = STRINGS_BY_ACTION_TYPE.get(workType);

        String taskOrder = STRAIGHT_FORMATTED_CURRENT_DATE + " - " + otoWorksStrings.get(2) + switch (workType) {

            case "WK", "NOT", "meterSupply", "dcSupply", "dcRestart" -> {
                if (dataParts.length > 1) {
                    if (!dataContainsNot123) yield " " + dataParts[1];
                    else {
                        int firstSpace = dataParts[1].indexOf(" ");
                        int secondSpace = dataParts[1].indexOf(" ", firstSpace + 1);
                        yield dataParts[1].substring(0, secondSpace) + " № " + deviceNumber + dataParts[1].substring(secondSpace);
                    }
                } else yield "";
            }

            case "meterChange" -> {
                meterManagementService.changeMeter(deviceNumber, otoRow, deviceNumberColumnIndex, dataParts);

                yield deviceNumber + " (" + dataParts[1]
                        + " кВт) на " + dataParts[2] + " (" + dataParts[3] + " кВт). Причина замены: " + dataParts[4] + ".";
            }
            case "ttChange" ->
                    String.format("%s, номиналом %s, с классом точности %s, %sг.в. №АВС = %s, %s, %s. Причина замены: %s.",
                            dataParts[1], dataParts[2], dataParts[3], dataParts[4],
                            dataParts[5], dataParts[6], dataParts[7], dataParts[8]);
            case "dcChange" -> {
                otoRow.getCell(deviceNumberColumnIndex).setCellValue(dataParts[1]);
                yield String.format("%s на концентратор № %s. Причина замены: %s.", deviceNumber, dataParts[1], dataParts[2]);
            }
            case "iikMount" -> {
                meteringPointManagementService.createMeteringPoint(otoRow, deviceNumberColumnIndex, dataParts);
                yield "";
            }
            default -> null;
        };
        copyAndFillLogRow(otoRow, newLogRow, orderColumnNumber, taskOrder, otoWorksStrings);

        return taskOrder;
    }

    private int[] getIndexesOfCleaningCells(String[] columnNames, Sheet sheet) {
        return Arrays.stream(columnNames)
                .mapToInt(name -> findColumnIndex(sheet, name))
                .filter(index -> index >= 0)
                .toArray();
    }

    public void createNewMeteringPointInExcelFile(String[] dataParts, Row otoRow, int deviceNumberColumnIndex) {
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

    public void meterChangeInExcelFile(Row otoRow, int deviceNumberColumnIndex, String mountingMeterNumber) {
        Object mountingDeviceNumber = parseMeterNumber(mountingMeterNumber);
        // Внесение номера Устройства в журнал "Контроль ПУ РРЭ"
        if (mountingDeviceNumber instanceof Long) {
            otoRow.getCell(deviceNumberColumnIndex).setCellValue((Long) mountingDeviceNumber);
        } else {
            otoRow.getCell(deviceNumberColumnIndex).setCellValue((String) mountingDeviceNumber);
        }
    }
}