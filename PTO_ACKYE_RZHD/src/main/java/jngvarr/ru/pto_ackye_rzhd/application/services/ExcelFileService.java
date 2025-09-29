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

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import static jngvarr.ru.pto_ackye_rzhd.application.constant.ExcelConstants.*;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.*;
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

    public void copyRow(Row sourceRow, Row targetRow, int columnCount) {
        for (int i = 0; i <= columnCount; i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);
            Workbook operationLog = targetRow.getSheet().getWorkbook();

            CellStyle defaultCellStyle = ExcelUtil.createCommonCellStyle(operationLog);
            CellStyle dateCellStyle = ExcelUtil.createDateCellStyle(operationLog, "dd.MM.YYYY", "Calibri");

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


    public void clearCellData(int[] ints, Row row) {
        for (int anInt : ints) {
            row.createCell(anInt).setCellValue("");
        }
    }

    public Map<String, String> getPhotoSavingPathFromExcel() {

        Map<String, String> paths = null;
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH))) {
            paths = new HashMap<>();
            Sheet iikSheet = planOTOWorkbook.getSheet("ИИК");
            int meterNumberColumnIndex = ExcelUtil.findColumnIndexFirstRowHeader(iikSheet, "Номер счетчика");
            int dcNumberColumnIndex = ExcelUtil.findColumnIndexFirstRowHeader(iikSheet, "Номер УСПД");
            int eelColumnIndex = ExcelUtil.findColumnIndexFirstRowHeader(iikSheet, "ЭЭЛ");
            int stationColumnIndex = ExcelUtil.findColumnIndexFirstRowHeader(iikSheet, "Железнодорожная станция");
            int substationColumnIndex = ExcelUtil.findColumnIndexFirstRowHeader(iikSheet, "ТП/КТП");
            int meterPointIndex = ExcelUtil.findColumnIndexFirstRowHeader(iikSheet, "Точка учёта");
            for (Row row : iikSheet) {
                String meterNum = ExcelUtil.getCellStringValue(row.getCell(meterNumberColumnIndex));
                String dcNum = ExcelUtil.getCellStringValue(row.getCell(dcNumberColumnIndex));
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

    private void fillDcSection(Sheet dcWorkSheet, String taskOrder, boolean isDcChange) { // заполнение данных на вкладке "ИВКЭ"
        int dcNumberColIndex = ExcelUtil.findColumnIndexFirstRowHeader(dcWorkSheet, "Серийный номер концентратора");
        int dcCurrentStateColIndex = ExcelUtil.findColumnIndexFirstRowHeader(dcWorkSheet, "Состояние ИВКЭ");

        for (Row row : dcWorkSheet) {
            String deviceNumber = ExcelUtil.getCellStringValue(row.getCell(dcNumberColIndex));
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
        ExcelUtil.setDateCellStyle(date);
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
        int orderColumnNumber = ExcelUtil.findColumnIndexFirstRowHeader(meterSheet, "Отчет бригады о выполнении ОТО");
        int deviceNumberColumnIndex = ExcelUtil.findColumnIndexFirstRowHeader(meterSheet, isDcWorks ? "Номер УСПД" : "Номер счетчика");
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
            String deviceNumber = ExcelUtil.getCellStringValue(otoRow.getCell(deviceNumberColumnIndex));
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
                    otoRow.getCell(ExcelUtil.findColumnIndexFirstRowHeader(meterSheet, "Текущее состояние")).setCellValue(notType);
                }
                if (dataContainsNotNot5) {
                    String notType = taskOrder.substring(taskOrder.indexOf("НОТ"), taskOrder.indexOf("НОТ") + 3);
                    otoRow.getCell(ExcelUtil.findColumnIndexFirstRowHeader(meterSheet, "Текущее состояние")).setCellValue(notType);
                }
            }
        }
//        addedRows = 0;
        return taskOrder;
    }

    public void fillDbWithData(Sheet ivkeSheet, Sheet iikSheet) {
        fillDbWithIvkeData(ivkeSheet);
//        dcManagementService.saveDcFromMap();//?
        fillDbWithIikData(iikSheet);
//        dcManagementService.saveDcFromMap(); //TODO переделать
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

    public void addDataFromIikContent(String dataFilePath) {

        long startTime = System.currentTimeMillis();
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(dataFilePath))
        ) {
            fillDbWithIikContent(planOTOWorkbook.getSheetAt(0));

            log.info("Data filled successfully!");

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution time: " + duration / 1000 / 60 + " minutes");
    }

    public void fillDbWithIvkeData(Sheet sheet) {
        log.info("Выгружаем DC из бд: ");
        dcManagementService.putAllDcToCache(dcService.getAllDc());
        log.info("Проверяем наличие новых DC из файла в БД: ");
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Пропускаем первую строку, это заголовок
                continue;
            }
            String dcNumber = ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_DC_NUMBER));
            if (!entityCache.get(EntityType.DC).containsKey(dcNumber) && dcNumber != null) {
                Substation substation = substationManagementService.createSubstationIfNotExists(ExcelUtil.createSubstationDto(row));
                dcManagementService.createDc(substation, dcNumber, getDcDataFromRow(row, false));
                log.info("Сохраняем DC {} из строки {} : ", dcNumber, row.getRowNum());
            }
        }
    }

    private String[] getDcDataFromRow(Row row, boolean dataFromContent) {
        String[] data = new String[2];
        if (!dataFromContent) {
            data[0] = ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_BUS_SECTION_NUM));
            data[1] = ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_DC_INSTALLATION_DATE));
        }
        return data;
    }

    public void fillDbWithIikContent(Sheet sheet) {
        log.info("Выгружаем DC из бд: ");
        dcManagementService.putAllDcToCache(dcService.getAllDc());
        log.info("Выгружаем ИИК из бд: ");
        meteringPointManagementService.putAllMeteringPointToCache(meteringPointService.getAllIik());
        log.info("Выгружаем счетчики из бд: ");
        meterManagementService.putAllMeterToCache(meterService.getAllMeters());
//        Map<Long, MeteringPoint> newMeteringPoints = new HashMap<>();



        for (Row row : sheet) {
            if (row.getRowNum() < 4) {
                // Пропускаем первую строку, это заголовок
                continue;
            }
            String newIikId = ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Идентификатор ТУ", 1)));
            String dcNumber = ExcelUtil.getCellStringValue(row.getCell(ExcelUtil.findColumnIndexAnotherRowHeader(sheet, "Сер. ном. УСПД", 1)));

            if (!entityCache.get(EntityType.DC).containsKey(dcNumber) && dcNumber != null && !dcNumber.isBlank()) {
                Substation substation = substationManagementService.createSubstationIfNotExists(ExcelUtil.createSubstationDtoFromContent(row));
                dcManagementService.createDc(substation, dcNumber, getDcDataFromRow(row, true));
            }

            if (!entityCache.get(EntityType.METERING_POINT).containsKey(newIikId)) {
                int rowNum = row.getRowNum();
                log.info("Создаем новый ИИК с id = {} из строки {}.", newIikId, rowNum);
                Meter newMeter = meterManagementService.constructMeterFromIikContent(row);
                MeteringPoint newIik = meteringPointManagementService.constructIIkFromIikContent(row);
                if (dcNumber == null) {
                    if (newIik.getMeteringPointAddress() == null) newIik.setMeteringPointAddress("");
                } else {
                    Dc dcByNumber = dcService.getDcByNumber(dcNumber);

                    if (dcByNumber != null) {
                        newMeter.setDc(dcByNumber);
                        if (!entityCache.get(EntityType.METER).containsKey(newMeter.getMeterNumber())) {
                            newMeter = meterService.create(newMeter);
                        } else newMeter = meterService.getMeterByNumber(newMeter.getMeterNumber());
                        dcByNumber.getMeters().add(newMeter);
                        entityCache.get(EntityType.DC).put(dcByNumber.getDcNumber(), dcByNumber);
                        dcService.updateDc(dcByNumber, dcByNumber.getId());
                    }
                }
                newIik.setMeter(newMeter);
//                newMeteringPoints.put(newIik.getId(), newIik);
                meteringPointService.create( newIik);
                entityCache.get(EntityType.METERING_POINT).put(String.valueOf(newIik.getId()), newIik);
            }
        }
//        for (MeteringPoint point : newMeteringPoints.values()) {
//        }
    }

    public void ExportMeteringPointContent() {
        String filePath = "БД.xlsx";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("ИИК");


            Row headerRow = sheet.createRow(0);
            headerRow.createCell(1).setCellValue("Регион");
            headerRow.createCell(2).setCellValue("ЭЭЛ");
            headerRow.createCell(3).setCellValue("ЭЧ");
            headerRow.createCell(4).setCellValue("ЭЧС/ЭЧК");
            headerRow.createCell(5).setCellValue("Железнодорожная станция");
            headerRow.createCell(6).setCellValue("ТП/КТП");
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(7).setCellValue("Точка учёта");
            headerRow.createCell(8).setCellValue("Адрес установки");
            headerRow.createCell(9).setCellValue("Марка счётчика");
            headerRow.createCell(10).setCellValue("Номер счетчика");
            headerRow.createCell(11).setCellValue("Номер УСПД");
            headerRow.createCell(12).setCellValue("Дата монтажа ТУ");
            headerRow.createCell(13).setCellValue("Текущее состояние");
            headerRow.createCell(14).setCellValue("Счетчик в Горизонте отмечен как НОТ?");
            headerRow.createCell(15).setCellValue("ВСЕГО счетчиков на \nВСЕГО счетчиков на " + TODAY + "\n");
            headerRow.createCell(16).setCellValue("Статус счетчика в Горизонте на\n" + TODAY + "\n");
            headerRow.createCell(17).setCellValue("Задание на ОТО от диспетчера");
            headerRow.createCell(18).setCellValue("Отчет бригады о выполнении ОТО");

            int rowNum = 1;
            for (Object mp : entityCache.get(EntityType.METERING_POINT).values()) {
                MeteringPoint nmp = (MeteringPoint) mp;
                Row row = sheet.createRow(rowNum++);


                row.createCell(0).setCellValue(nmp.getId());
                headerRow.createCell(1).setCellValue(nmp.getSubstation().getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getRegion().getName());
                headerRow.createCell(2).setCellValue(nmp.getSubstation().getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getName());
                headerRow.createCell(3).setCellValue(nmp.getSubstation().getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getName());
                headerRow.createCell(4).setCellValue(nmp.getSubstation().getStation().getPowerSupplyDistrict().getName());
                headerRow.createCell(5).setCellValue(nmp.getSubstation().getStation().getName());
                headerRow.createCell(6).setCellValue(nmp.getSubstation().getName());
                headerRow.createCell(7).setCellValue(nmp.getName());
                headerRow.createCell(8).setCellValue(nmp.getMeteringPointAddress());
                headerRow.createCell(9).setCellValue(nmp.getMeter().getMeterModel());
                headerRow.createCell(10).setCellValue(nmp.getMeter().getMeterNumber());
                headerRow.createCell(11).setCellValue(nmp.getMeter().getDc().getDcNumber());
                headerRow.createCell(12).setCellValue(nmp.getInstallationDate());
//                headerRow.createCell(13).setCellValue("Текущее состояние");
//                headerRow.createCell(14).setCellValue("Счетчик в Горизонте отмечен как НОТ?");
//                headerRow.createCell(15).setCellValue("ВСЕГО счетчиков на \nВСЕГО счетчиков на " + TODAY + "\n");
//                headerRow.createCell(16).setCellValue("Статус счетчика в Горизонте на\n" + TODAY + "\n");
//                headerRow.createCell(17).setCellValue("Задание на ОТО от диспетчера");
//                headerRow.createCell(18).setCellValue("Отчет бригады о выполнении ОТО");

            }
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            System.out.println("Файл Excel успешно сохранён: " + filePath);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при экспорте в Excel", e);
        }
    }

    public void ExportOto() {
        String filePath = "БД.xlsx";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("ИИК");


            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Регион");
            headerRow.createCell(2).setCellValue("ЭЭЛ");
            headerRow.createCell(3).setCellValue("ЭЧ");
            headerRow.createCell(4).setCellValue("ЭЧС/ЭЧК");
            headerRow.createCell(5).setCellValue("Железнодорожная станция");
            headerRow.createCell(6).setCellValue("ТП/КТП");
            headerRow.createCell(7).setCellValue("Точка учёта");
            headerRow.createCell(8).setCellValue("Адрес установки");
            headerRow.createCell(9).setCellValue("Марка счётчика");
            headerRow.createCell(10).setCellValue("Номер счетчика");
            headerRow.createCell(11).setCellValue("Номер УСПД");
            headerRow.createCell(12).setCellValue("Дата монтажа ТУ");
            headerRow.createCell(13).setCellValue("Текущее состояние");
            headerRow.createCell(14).setCellValue("Счетчик в Горизонте отмечен как НОТ?");
            headerRow.createCell(15).setCellValue("ВСЕГО счетчиков на \nВСЕГО счетчиков на " + TODAY + "\n");
            headerRow.createCell(16).setCellValue("Статус счетчика в Горизонте на\n" + TODAY + "\n");
            headerRow.createCell(17).setCellValue("Задание на ОТО от диспетчера");
            headerRow.createCell(18).setCellValue("Отчет бригады о выполнении ОТО");

            int rowNum = 1;
            for (Object mp : entityCache.get(EntityType.METERING_POINT).values()) {
                MeteringPoint nmp = (MeteringPoint) mp;
                Row row = sheet.createRow(rowNum++);


                row.createCell(0).setCellValue(nmp.getId());
                headerRow.createCell(1).setCellValue(nmp.getSubstation().getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getRegion().getName());
                headerRow.createCell(2).setCellValue(nmp.getSubstation().getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getName());
                headerRow.createCell(3).setCellValue(nmp.getSubstation().getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getName());
                headerRow.createCell(4).setCellValue(nmp.getSubstation().getStation().getPowerSupplyDistrict().getName());
                headerRow.createCell(5).setCellValue(nmp.getSubstation().getStation().getName());
                headerRow.createCell(6).setCellValue(nmp.getSubstation().getName());
                headerRow.createCell(7).setCellValue(nmp.getName());
                headerRow.createCell(8).setCellValue(nmp.getMeteringPointAddress());
                headerRow.createCell(9).setCellValue(nmp.getMeter().getMeterModel());
                headerRow.createCell(10).setCellValue(nmp.getMeter().getMeterNumber());
                headerRow.createCell(11).setCellValue(nmp.getMeter().getDc().getDcNumber());
                headerRow.createCell(12).setCellValue(nmp.getInstallationDate());
//                headerRow.createCell(13).setCellValue("Текущее состояние");
//                headerRow.createCell(14).setCellValue("Счетчик в Горизонте отмечен как НОТ?");
//                headerRow.createCell(15).setCellValue("ВСЕГО счетчиков на \nВСЕГО счетчиков на " + TODAY + "\n");
//                headerRow.createCell(16).setCellValue("Статус счетчика в Горизонте на\n" + TODAY + "\n");
//                headerRow.createCell(17).setCellValue("Задание на ОТО от диспетчера");
//                headerRow.createCell(18).setCellValue("Отчет бригады о выполнении ОТО");

            }
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            System.out.println("Файл Excel успешно сохранён: " + filePath);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при экспорте в Excel", e);
        }
    }

    public void writeLog() {

    }

    public void convertXlToLog(String path) {
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(path));
             BufferedWriter bw = new BufferedWriter(new FileWriter("ОЖ.Log"))) {
            StringBuilder logString = new StringBuilder();
            for (Row row : workbook.getSheet("ОЖ")) {
                logString.setLength(0);
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    logString.append(ExcelUtil.getCellStringValue(row.getCell(i))).append(",");
                }
                if (!logString.isEmpty()) {
                    logString.deleteCharAt(logString.length() - 1);
                }
                bw.write(logString.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void fillDbWithIikData(Sheet sheet) {
        log.info("Выгружаем ИИК из бд: ");
//        Map<Long, MeteringPoint> meteringPoints = new HashMap<>();
        meteringPointManagementService.putAllMeteringPointToCache(meteringPointService.getAllIik());
        Map<Long, MeteringPoint> newMeteringPoints = new HashMap<>();
//        for (MeteringPoint mp : meteringPointService.getAllIik()) {
//            meteringPoints.put(mp.getId(), mp);
//        }

        log.info("Выгружаем счетчики из бд: ");
        Map<String, Meter> meters = new HashMap<>();
        for (Meter m : meterService.getAllMeters()) {
            meters.put(m.getMeterNumber(), m);
        }

        log.info("Проверяем наличие новых ИИК из файла в БД: ");
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            String newIikId = ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ID));
            if (!entityCache.get(EntityType.METERING_POINT).containsKey(newIikId)) {
                int rowNum = row.getRowNum();
                log.info("Создаем новый ИИК с id = {} из строки {}.", newIikId, rowNum);
                Meter newMeter = meterManagementService.constructMeter(row);
                MeteringPoint newIik = meteringPointManagementService.constructIIk(row);
                String dcNumber = ExcelUtil.getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_DC_NUMBER));
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
                        } else newMeter = meterService.getMeterByNumber(newMeter.getMeterNumber());
//                        if (newMeter == null) newMeter = meterService.getMeterByNumber(newMeter.getMeterNumber());
                        dcByNumber.getMeters().add(newMeter);
                        entityCache.get(EntityType.DC).put(dcByNumber.getDcNumber(), dcByNumber);
                        dcService.updateDc(dcByNumber, dcByNumber.getId());
                    }
                }
                newIik.setMeter(newMeter);
                newMeteringPoints.put(newIik.getId(), newIik);
                entityCache.get(EntityType.METERING_POINT).put(String.valueOf(newIik.getId()), newIik);
            }
        }
        for (MeteringPoint point : newMeteringPoints.values()) {
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
                .mapToInt(name -> ExcelUtil.findColumnIndexFirstRowHeader(sheet, name))
                .filter(index -> index >= 0)
                .toArray();
    }

}