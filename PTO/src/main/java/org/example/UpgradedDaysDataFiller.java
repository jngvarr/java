package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.example.ExcelSplitter.findColumnIndex;

public class UpgradedDaysDataFiller { //заполнение файла Контроль ПУ РРЭ (Задания на ОТО РРЭ)

    private static final Logger logger = LoggerFactory.getLogger(UpgradedDaysDataFiller.class);
    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_DDMMMM = DateTimeFormatter.ofPattern("dd MMMM", new Locale("ru"));
    private static final String PLAN_OTO_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx";
    private static final LocalDate TODAY = LocalDate.now();
    private static final String PRESENT_MONTH_IN_RUSSIAN = TODAY.format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru-RU"))).toUpperCase();
    static final String PLAN_PTO_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ПТО\\";
    private static final String PLAN_PTO = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ПТО\\СВОД_ИИК ПТО РРЭ 2025_" + PRESENT_MONTH_IN_RUSSIAN + ".xlsx";
    private static final String FOLDER_PATH = "d:\\Downloads\\пто\\reports\\" + TODAY.format(DATE_FORMATTER_DDMMYYYY);
    private static final String RESERVE_FILE_DATE = TODAY.format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru-RU"))).toUpperCase() + " " + TODAY.getYear();
    private static final String CLOUD_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\АРХИВ РРЭ\\Архив заданий на ОТО\\";
    private static final String COUNTER_NUMBER_CELL = "Номер счетчика";
    private static final String ID_CELL = "ID";
    private static final String EEL_CELL = "ЭЭЛ";
    private static final String IIK_STATUS_CELL = "Статус счетчика";
    private static final String NORMALLY_TURNED_OFF_CELL = "Счетчик в Горизонте отмечен как НОТ";
    private static final String TASK_CELL = "Задание на ОТО от диспетчера";
    private static final String CONNECTION_DATE_CELL = "Дата получения статуса";

    private static final String DATA_CONTROL_METER_NUMBER_COLUMN_NAME = "Счетчик";
    private static final String DATA_CONTROL_DATA_COLUMN_NAME = "Достоверность";
    private static final String NORMALLY_TURNED_OFF_METER_NUMBER_COLUMN_NAME = "Сер. номер ПУ";
    private static final String NORMALLY_TURNED_OFF_DATA_COLUMN_NAME = "Нор. откл.";
    private static final String IIK_STATUS_METER_NUMBER_COLUMN_NAME = "Серийный номер ПУ";
    private static final String IIK_STATUS_DATA_COLUMN_NAME = "Статус";
    private static final String CONNECTION_DIAG_DC_NUMBER_COLUMN_NAME = "Серийный номер";
    private static final String CONNECTION_DIAG_DATA_COLUMN_NAME = "Дата успешной проверки";

    private enum DataType {
        DATA_CONTROL, NORMALLY_TURNED_OFF, IIK_STATUS, CONNECTION_DIAG
    }

    private static final Map<String, Integer> iikCountMap = new HashMap<>();
    private static final Map<String, String> iikStatesMap = new HashMap<>();
    static Map<String, String> synchroMap = new HashMap<>();
    static Set<String> changedRows = new HashSet<>();

    private static final boolean needSynchronize = isSynchronizeNeeded();

    private static boolean isSynchronizeNeeded() {
        return true;
        //        return !synchroMap.isEmpty();
    }

    static Map<DataType, Map<String, String>> dataMaps = new EnumMap<>(DataType.class);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        dataMaps = new EnumMap<>(DataType.class);

        for (DataType type : DataType.values()) {
            dataMaps.put(type, new HashMap<>());
        }

        File[] files = new File(FOLDER_PATH).listFiles((dir, name) -> name.endsWith(".xlsx"));
        if (files == null || files.length == 0) {
            logger.info("No files found in folder: " + FOLDER_PATH);
            return;
        }
//            File[] files1 = new File(PLAN_PTO_PATH).listFiles((dir, name) -> name.startsWith("СВОД_ИИК ПТО РРЭ "));
//        File planPTO = (files1 != null && files1.length > 0) ? files1[0] : null;

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            for (File file : files) {
                executorService.submit(() -> processFile(file, dataMaps));
            }
        } finally {
            executorService.shutdown();
            try {
                executorService.awaitTermination(2, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                logger.error("Data processing interrupted", e);
            }
        }

        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             FileOutputStream fileOut = new FileOutputStream(PLAN_OTO_PATH);
        ) {
            Sheet otoIikSheet = planOTOWorkbook.getSheet("ИИК");

            if (needSynchronize) synchronize(otoIikSheet);
            fillIIKData(otoIikSheet);
            fillIVKEData(planOTOWorkbook.getSheet("ИВКЭ"));
            planOTOWorkbook.write(fileOut);
            logger.info("Data filled successfully!");
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Execution time: " + duration / 1000 + " seconds");
            createReserveCopy(planOTOWorkbook);

            if (needSynchronize) {
                ExcelMerger.main(args);
                Workbook planPTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_PTO));
                Sheet ptoIikSheet = planPTOWorkbook.getSheet("Свод");
                FileOutputStream filePtoOut = new FileOutputStream(PLAN_PTO);
                synchronize(ptoIikSheet);
                planPTOWorkbook.write(filePtoOut);
                planPTOWorkbook.close();
                ExcelSplitter.main(args);
                deleteSummaryFiles();
                synchronize(otoIikSheet);
            }
            EmailSenderMultipleRecipients.main(args); // рассылка "Контроль ПУ РРЭ"


        } catch (IOException ex) {
            logger.error("Error processing workbook", ex);
        }
    }

//    static void deleteSummaryFiles() {
//        File folder = new File(PLAN_PTO_PATH);
//        File[] files = folder.listFiles((dir, name) -> name.contains("СВОД"));
//
//        if (files == null || files.length == 0) {
//            logger.info("Нет файлов для удаления в папке: " + folder);
//            return; // <--- важно! иначе ниже будет NPE
//        }
//
//        for (File file : files) {
//            if (file.delete()) {
//                logger.info("Удалён файл: " + file.getName());
//            } else {
//                logger.warn("Не удалось удалить файл: " + file.getName());
//            }
//        }
//    }

    private static void deleteSummaryFiles() {
        File folder = new File(PLAN_PTO_PATH);
        Optional.ofNullable(folder.listFiles((d, n) -> n.contains("СВОД")))
                .ifPresent(files -> Arrays.stream(files).forEach(File::delete));
        logger.info("Summary files deleted. ");
    }

    private static void createReserveCopy(Workbook planOTOWorkbook) {
        File cloudDir = new File(CLOUD_PATH);
        if (!cloudDir.exists()) {
            cloudDir.mkdirs(); // Создаем папку, если её нет
        }
        File reserveFile = new File(CLOUD_PATH + "Контроль ПУ РРЭ (Задания на ОТО РРЭ) " + RESERVE_FILE_DATE + ".xlsx");

        if (!reserveFile.exists()) {
            try (FileOutputStream reserveFileOut = new FileOutputStream(reserveFile)) {
                planOTOWorkbook.write(reserveFileOut);
                logger.info("Reserve file saved: " + reserveFile.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save reserve copy", e);
            }
        } else {
            logger.info("Reserve file already exists: " + reserveFile.getName());
        }
    }

    private static void processFile(File file, Map<DataType, Map<String, String>> dataMaps) {
        String fileName = file.getName();
        try {
            if (fileName.startsWith("Контроль поступления данных")) {
                dataMaps.get(DataType.DATA_CONTROL).putAll(fillingMapWithData(DATA_CONTROL_METER_NUMBER_COLUMN_NAME, DATA_CONTROL_DATA_COLUMN_NAME, file));
            } else if (fileName.startsWith("Состав ИИК")) {
                dataMaps.get(DataType.NORMALLY_TURNED_OFF).putAll(fillingMapWithData(NORMALLY_TURNED_OFF_METER_NUMBER_COLUMN_NAME, NORMALLY_TURNED_OFF_DATA_COLUMN_NAME, file));
            } else if (fileName.startsWith("Статусы ПУ")) {
                dataMaps.get(DataType.IIK_STATUS).putAll(fillingMapWithData(IIK_STATUS_METER_NUMBER_COLUMN_NAME, IIK_STATUS_DATA_COLUMN_NAME, file));
            } else if (fileName.startsWith("Диагностика связи")) {
                dataMaps.get(DataType.CONNECTION_DIAG).putAll(fillingMapWithData(CONNECTION_DIAG_DC_NUMBER_COLUMN_NAME, CONNECTION_DIAG_DATA_COLUMN_NAME, file));
            }
            logger.info("Processed file: " + fileName);
        } catch (Exception e) {
            logger.error("Error processing file: " + fileName, e);
        }
    }

    private static Map<String, String> fillingMapWithData(String deviceNumberColumnName, String neededDataColumnName, File file) {
        Map<String, String> workMap = new HashMap<>();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
            Sheet sheet = workbook.getSheetAt(0);
            int headersRowNum = getHeaderIndex(file.getName());
            int deviceNumberColumnIndex = findColumnIndex(sheet, deviceNumberColumnName, headersRowNum);
            int neededDataColumnIndex = findColumnIndex(sheet, neededDataColumnName, headersRowNum);
            boolean isStatusPUFile = file.getName().contains("Статусы ПУ");
            for (Row row : sheet) {
                String key = getCellStringValue(row.getCell(deviceNumberColumnIndex));
                String value = getCellStringValue(row.getCell(neededDataColumnIndex));
                if (isStatusPUFile) {
                    Cell statusDateCell = row.getCell(neededDataColumnIndex + 1);
                    value += "_" + (statusDateCell != null ? getCellStringValue(statusDateCell) : "");
                }
                if (file.getName().contains("Состав ИИК")) {
                    String dcNum = getCellStringValue(row.getCell(
                            findColumnIndex(sheet, "Сер. ном. УСПД", 1)));
                    iikCountMap.put(dcNum, iikCountMap.getOrDefault(dcNum, 0) + 1);
                    if (needSynchronize) {// TODO что-то здесь надо доделать // синхронизируется весь файл
                        if (!needToSyncRow(row)) continue;
                        synchroMapCreating(row);
                    }
                }
                if (key != null && value != null) {
                    workMap.put(key.trim(), value);
                }
            }
        } catch (IOException ex) {
            logger.error("Error reading file: " + file.getName(), ex);
        }
//        synchronizeData(synchroMap);
        return workMap;
    }

    private static int getHeaderIndex(String name) {
        Map<String, Integer> headerRows = Map.of(
                "Контроль поступления данных c", 0,
                "Состав ИИК ", 1,
                "Статусы ПУ (Echelon)", 3,
                "Диагностика связи с УСПД-ПУ", 4
        );

        return headerRows.entrySet().stream()
                .filter(e -> name.startsWith(e.getKey()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(" файла: " + name))
                .getValue();
    }

    private static void synchroMapCreating(Row row) {
        String synchroMapKey = getCellStringValue(row.getCell(
                findColumnIndex(row.getSheet(), "Идентификатор ТУ", 1)));
        try {
            Long.parseLong(synchroMapKey);
            synchroMap.put(synchroMapKey, getSyncData(row));
        } catch (NumberFormatException ignored) {
        }
//        if (!key.matches("\\d+")) return; //Проверка, что строка полностью состоит из цифр

    }

    private static String getSyncData(Row row) {
        Sheet workSheet = row.getSheet();
        return new StringJoiner("_").
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Идентификатор ТУ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Регион", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЭЭЛ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЭЧ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЭЧС", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЖД станция", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЭЧЭ/ТП/КТП", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Присоединение", 2)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Название ТУ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Размещение ПУ", 2)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Адрес ТУ", 2)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Модель ПУ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Сер. номер ПУ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Сер. ном. УСПД", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Дата монт.", 1)))).
                toString();
    }

    private static void fillIIKData(Sheet worksheet) {
        Row firstRow = worksheet.getRow(0);
        int lastColumnNum = firstRow.getLastCellNum();
        int enabledCount = 0;
        CellStyle commonCS = createCommonCellStyle(worksheet);
        CellStyle dateCS = createDateCellStyle(worksheet.getWorkbook());
        int iikQuantityCellNumber = findColumnIndex(worksheet, "ВСЕГО счетчиков на концентраторе", null);
        int dcCellNumber = findColumnIndex(worksheet, "Номер УСПД", null);

        for (Row row : worksheet) {
            String key = getCellStringValue(row.getCell(findColumnIndex(row.getSheet(), COUNTER_NUMBER_CELL, null)));
            if (key == null) continue;
            fillIiksStateMap(row, worksheet);
            String dcNum = getCellStringValue(row.getCell(dcCellNumber));
            if (iikCountMap.containsKey(dcNum) && !dcNum.isEmpty()) {
                Cell cell = row.getCell(iikQuantityCellNumber);
                cell = cell == null ? row.createCell(iikQuantityCellNumber) : cell;
                int value = iikCountMap.getOrDefault(dcNum, 0);
                cell.setCellValue(value);
                cell.setCellStyle(commonCS);
            }

            key = key.trim();

            boolean isNormallyTurnedOff = false;
            boolean hasWrongKey = false;

            if (dataMaps.get(DataType.IIK_STATUS).containsKey(key)) {
                Cell iikStatusCell = row.createCell(findColumnIndex(row.getSheet(), IIK_STATUS_CELL, null));
                Cell connectionDateCell = row.createCell(findColumnIndex(row.getSheet(), CONNECTION_DATE_CELL, null));
                String iikStatusData = dataMaps.get(DataType.IIK_STATUS).get(key);
                if (!"_".equals(iikStatusData)) {
                    String[] iikStatusValues = iikStatusData.split("_");
                    String iikStatus = iikStatusValues[0].trim();
                    iikStatusCell.setCellValue(iikStatus); // TODO : разобраться со статусами и выставлением заданий
                    iikStatusCell.setCellStyle(commonCS);
                    connectionDateCell.setCellValue(iikStatusValues[1]);

                    // Устанавливаем стиль даты
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    try {
                        connectionDateCell.setCellValue(sdf.parse(iikStatusValues[1]));
                    } catch (ParseException e) {
                        connectionDateCell.setCellValue(iikStatusValues[1]);
                    }
                    connectionDateCell.setCellStyle(dateCS);
                    hasWrongKey = iikStatus.equals("Неверный ключ аутентификации");
                }
            }
            if (dataMaps.get(DataType.NORMALLY_TURNED_OFF).containsKey(key)) {
                Cell cell = row.createCell(findColumnIndex(row.getSheet(), NORMALLY_TURNED_OFF_CELL, null));
                String normallyTurnedOff = dataMaps.get(DataType.NORMALLY_TURNED_OFF).get(key);
                cell.setCellValue(normallyTurnedOff);
                cell.setCellStyle(commonCS);
                isNormallyTurnedOff = normallyTurnedOff.equals("Да");
            }
            if (dataMaps.get(DataType.DATA_CONTROL).containsKey(key)) {
                Cell cell = row.createCell(lastColumnNum);
                int ind = findColumnIndex(row.getSheet(), TASK_CELL, null);
//                logger.info("291 строка номер {}", row.getRowNum());
                Cell taskCell = row.createCell(ind);
                String profile = dataMaps.get(DataType.DATA_CONTROL).get(key);
                if ("Достоверные".equals(profile)) {
                    taskCell.setCellValue("");
                    enabledCount++;
                } else if (!isNormallyTurnedOff) {
                    taskCell.setCellValue("Выезд нужен - счетчик не отдает показания");
                }
                if (hasWrongKey) taskCell.setCellValue("Выезд нужен - WrongKey");
                cell.setCellValue(profile);
                taskCell.setCellStyle(commonCS);
            }
        }
        setTopRowProperties(firstRow, lastColumnNum, enabledCount);
    }

    private static void fillIiksStateMap(Row row, Sheet worksheet) {
        int iiksCurrentStateCellNumber = findColumnIndex(worksheet, "Текущее состояние", null);
        int iiksIdCellNumber = findColumnIndex(worksheet, "ID", null);
        iikStatesMap.put(getCellStringValue(row.getCell(iiksIdCellNumber)), getCellStringValue(row.getCell(iiksCurrentStateCellNumber)));
    }

    private static void synchronize(Sheet worksheet) {
        synchronizeDb();
        synchronizeExcel(worksheet);
    }

    private static void synchronizeDb() {
//        Set <String> iikIds =
//         synchroMap
    }


    private static void synchronizeExcel(Sheet worksheet) {
        Set<String> ids = new HashSet<>(synchroMap.keySet());
        for (Row row : worksheet) {
            String key = getCellStringValue(row.getCell(findColumnIndex(row.getSheet(), ID_CELL, null)));
            if (key == null) continue;
            key = key.trim();
            if (synchroMap.containsKey(key)) {
                synchronizeRow(key, row);
            }
//            if (key.matches("\\d+"))
            ids.remove(key);
        }

        if (!ids.isEmpty()) {
            for (String id : ids) {
                Row newRow = worksheet.createRow(worksheet.getLastRowNum() + 1);
                synchronizeRow(id, newRow);
            }
        }
    }

    private static boolean needToSyncRow(Row row) {
        String eel = getCellStringValue(row.getCell(findColumnIndex(row.getSheet(), EEL_CELL, 1)));
        return eel.contains("ЭЭЛ-");
    }

    private static boolean rowHasChange(String iikId) {
        return changedRows.contains(iikId);
    }

    private static void synchronizeRow(String key, Row row) {
        String[] values = synchroMap.get(key).split("_", -1);
        for (int i = 0; i < values.length; i++) {
            if (row.getSheet().getSheetName().contains("Свод")) {
                addAdditionalCell(row);
            }
            if (values[i].matches("\\d+")) {
                row.createCell(i).setCellValue(Long.parseLong(values[i]));
            } else {
                row.createCell(i).setCellValue(values[i]);
            }
        }
    }

    private static void addAdditionalCell(Row row) {
        String iiksId = getCellStringValue(row.getCell(findColumnIndex(row.getSheet(), "ID", null)));
        Cell iikTypeCell = row.createCell(findColumnIndex(row.getSheet(), "Тип ИИК", null));
        Cell notStatusCell = row.createCell(findColumnIndex(row.getSheet(), "Статус (НОТ)", null));
//        logger.info("358 строка номер {}", row.getRowNum());
        int inx = findColumnIndex(row.getSheet(), "Марка счётчика", null);
        Cell cell = row.getCell(inx);
        String meterNum = getCellStringValue(row.getCell(findColumnIndex(row.getSheet(),
                "Номер счетчика", null)));

        String meterType = (getCellStringValue(cell));
        meterType = meterType.getBytes().length > 0 ? meterType.replaceAll("\\D+", "") : meterType;
        String iikType =
                switch (meterType) {
                    case "1021" -> "1ф";
                    case "1023" -> "3ф";
                    case "2023" -> "3фт";
                    default -> "";
                };

        iikTypeCell.setCellValue(iikType);
//        String notStatus = dataMaps.get(DataType.NORMALLY_TURNED_OFF).get(meterNum);
//        notStatus = notStatus != null && notStatus.toLowerCase().trim().equals("да") ? "НОТ" : "В работе";
        notStatusCell.setCellValue(iikStatesMap.get(iiksId));
    }


    private static void setTopRowProperties(Row firstRow, int lastColumnNum, int enabledCount) {
        String formattedDate = LocalDate.now().format(DATE_FORMATTER_DDMMMM);
        Cell meterStatus = firstRow.getCell(findColumnIndex(firstRow.getSheet(), "Статус счетчика в Горизонте", null));
        meterStatus.setCellValue("Статус счетчика в Горизонте на " + LocalDate.now().format(DATE_FORMATTER_DDMMYYYY));
        formattedDate = formattedDate.substring(0, 3) + formattedDate.substring(3, 4).toUpperCase() + formattedDate.substring(4);
        int sourceColumnWidth = firstRow.getSheet().getColumnWidth(lastColumnNum - 1);
        Cell sourceCell = firstRow.getCell(lastColumnNum - 1);
        CellStyle cs = sourceCell != null ? sourceCell.getCellStyle() : firstRow.getSheet().getWorkbook().createCellStyle();
        Cell summaryCell = firstRow.createCell(lastColumnNum);
        summaryCell.setCellValue("Профили на " + formattedDate + " на интервале последних 7 дней (" + enabledCount + ")");
        summaryCell.setCellStyle(cs);
        Sheet workSheet = firstRow.getSheet();
        workSheet.setColumnWidth(lastColumnNum, sourceColumnWidth);
        int lastRowNum = workSheet.getLastRowNum();
        int firstColNum = 0;
        int firstRowNum = 0;

        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRowNum, lastRowNum, firstColNum, lastColumnNum);
        workSheet.setAutoFilter(cellRangeAddress);
        setCellAlignment(firstRowNum, lastRowNum, firstColNum, lastColumnNum, workSheet);

    }


    //подача на холостом ходу
    private static void setCellAlignment(int firstRowNum, int lastRowNum, int firstColNum,
                                         int lastColumnNum, Sheet worksheet) {
        Cell sourceC = worksheet.getRow(1).getCell(lastColumnNum - 1);
        CellStyle sc = (sourceC != null) ? sourceC.getCellStyle() : worksheet.getWorkbook().createCellStyle();
        sc.setAlignment(HorizontalAlignment.LEFT);
        sc.setVerticalAlignment(VerticalAlignment.CENTER);
        for (int rowIndex = firstRowNum + 1; rowIndex <= lastRowNum; rowIndex++) {
            Row row = worksheet.getRow(rowIndex);
            if (row == null) {
                row = worksheet.createRow(rowIndex); // Создаем строку, если она не существует
            }
            for (int cellIndex = firstColNum + 23; cellIndex <= lastColumnNum; cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                if (cell == null) {
                    cell = row.createCell(cellIndex); // Создаем ячейку, если она не существует
                }
                if (cell.getCellType() == CellType.BLANK || (cell.getCellType() == CellType.STRING
                        && cell.getStringCellValue().trim().isEmpty())) {
                    cell.setCellValue("#Н/Д");
                }
                cell.setCellStyle(sc);
            }
        }
    }

    private static void fillIVKEData(Sheet ivkeSheet) {
        DataFormat poiDataFormat = ivkeSheet.getWorkbook().createDataFormat();
        CellStyle dateCellStyle = ivkeSheet.getWorkbook().createCellStyle();
        dateCellStyle.setDataFormat(poiDataFormat.getFormat("dd.MM.yyyy"));
        int dcCellNum = findColumnIndex(ivkeSheet, "Серийный номер концентратора", null);
        int connectionDiagCellIndex = findColumnIndex(ivkeSheet, "Дата последней связи с УСПД", null);
        int iikQuantityCellIndex = findColumnIndex(ivkeSheet, "ВСЕГО счетчиков на концентраторе", null);
        for (Row row : ivkeSheet) {
            Cell ivkeCell = row.getCell(dcCellNum);
            String ivkeNumber = getCellStringValue(ivkeCell);
            if (ivkeNumber != null) {
                String key = ivkeNumber.trim();
                if (dataMaps.get(DataType.CONNECTION_DIAG).containsKey(key)) {
                    Cell connectionDiagCol = row.createCell(connectionDiagCellIndex);
                    Cell iikQuantity = row.getCell(iikQuantityCellIndex);
                    iikQuantity.setCellValue(iikCountMap.getOrDefault(key, 0));
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

    private static CellStyle createDateCellStyle(Workbook resultWorkbook) {
        CellStyle dateCellStyle = resultWorkbook.createCellStyle();
        DataFormat dateFormat = resultWorkbook.createDataFormat(); // Формат даты
        dateCellStyle.setDataFormat(dateFormat.getFormat("dd.MM.YYYY"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setFont(createCellFontStyle(resultWorkbook, "Calibri", (short) 10, false));
        return dateCellStyle;
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
                    return "";
            }
        }
        return "";
    }


    static CellStyle createCommonCellStyle(Sheet resultSheet) {
        CellStyle simpleCellStyle = resultSheet.getWorkbook().createCellStyle();
        Font font = createCellFontStyle(resultSheet.getWorkbook(), "Arial", (short) 10, false);

        simpleCellStyle.setBorderBottom(BorderStyle.THIN);
        simpleCellStyle.setBorderLeft(BorderStyle.THIN);
        simpleCellStyle.setBorderRight(BorderStyle.THIN);
        simpleCellStyle.setBorderTop(BorderStyle.THIN);
        simpleCellStyle.setFont(font);
        return simpleCellStyle;
    }

    private static Font createCellFontStyle(Workbook workbook, String fontName, short fontSize, boolean isBold) {
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        font.setBold(isBold);
        return font;
    }
}

