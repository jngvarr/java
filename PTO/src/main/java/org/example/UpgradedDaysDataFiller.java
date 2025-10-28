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
    private static final String FOLDER_PATH = "d:\\Downloads\\пто\\reports\\" + LocalDate.now().format(DATE_FORMATTER_DDMMYYYY);
    private static final LocalDate TODAY = LocalDate.now();
    private static final String RESERVE_FILE_DATE = TODAY.format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru-RU"))).toUpperCase() + " " + TODAY.getYear();
    private static final String CLOUD_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\АРХИВ РРЭ\\Архив заданий на ОТО\\";
    private static final int COUNTER_NUMBER_CELL_NUMBER = 13;
    private static final int IIK_STATUS_CELL_NUMBER = 19;
    private static final int NORMALLY_TURNED_OFF_CELL_NUMBER = 17;
    private static final int TASK_CELL_NUMBER = 20;
    private static final int CONNECTION_DATE_CELL_NUMBER = 22;

    private enum DataType {
        DATA_CONTROL, NORMALLY_TURNED_OFF, IIK_STATUS, CONNECTION_DIAG
    }

    private static final Map<String, Integer> iikCount = new HashMap<>();

    private static boolean needSynchronize = false;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Map<DataType, Map<String, String>> dataMaps = new EnumMap<>(DataType.class);

        for (DataType type : DataType.values()) {
            dataMaps.put(type, new HashMap<>());
        }

        File[] files = new File(FOLDER_PATH).listFiles((dir, name) -> name.endsWith(".xlsx"));
        if (files == null || files.length == 0) {
            logger.info("No files found in folder: " + FOLDER_PATH);
            return;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            for (File file : files) {
                executorService.submit(() -> processFile(file, dataMaps));
            }
        } finally {
            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                logger.error("Data processing interrupted", e);
            }
        }

        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             FileOutputStream fileOut = new FileOutputStream(PLAN_OTO_PATH)) {

            fillIIKData(planOTOWorkbook.getSheet("ИИК"), dataMaps);
            fillIVKEData(planOTOWorkbook.getSheet("ИВКЭ"), dataMaps);
            planOTOWorkbook.write(fileOut);
//            EmailSenderMultipleRecipients.main(args); // рассылка "Контроль ПУ РРЭ"
            createReserveCopy(planOTOWorkbook);


            logger.info("Data filled successfully!");

        } catch (IOException ex) {
            logger.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Execution time: " + duration / 1000 + " seconds");
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
                dataMaps.get(DataType.DATA_CONTROL).putAll(fillingMapWithData(1, 5, file));
            } else if (fileName.startsWith("Состав ИИК")) {
                dataMaps.get(DataType.NORMALLY_TURNED_OFF).putAll(fillingMapWithData(14, 9, file));
            } else if (fileName.startsWith("Статусы ПУ")) {
                dataMaps.get(DataType.IIK_STATUS).putAll(fillingMapWithData(11, 12, file));
            } else if (fileName.startsWith("Диагностика связи")) {
                dataMaps.get(DataType.CONNECTION_DIAG).putAll(fillingMapWithData(9, 11, file));
            }
            logger.info("Processed file: " + fileName);
        } catch (Exception e) {
            logger.error("Error processing file: " + fileName, e);
        }
    }

    private static Map<String, String> fillingMapWithData(int meterColumn, int neededDataColumn, File file) {
        Map<String, String> workMap = new HashMap<>();
        Map<String, String> synchroMap = new HashMap<>();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isStatusPUFile = file.getName().contains("Статусы ПУ");
            for (Row row : sheet) {
                String key = getCellStringValue(row.getCell(meterColumn));
                String value = getCellStringValue(row.getCell(neededDataColumn));
                if (isStatusPUFile) {
                    Cell statusDateCell = row.getCell(neededDataColumn + 1);
                    value += "_" + (statusDateCell != null ? getCellStringValue(statusDateCell) : "");
                }
                if (file.getName().contains("Состав ИИК")) {
                    String dcNum = getCellStringValue(row.getCell(
                            findColumnIndex(sheet, "Сер. ном. УСПД", 1)));
                    iikCount.put(dcNum, iikCount.getOrDefault(dcNum, 0) + 1);
                    if (needSynchronize) {// TODO что-то здесь надо доделать
                        synchroMap.putIfAbsent(
                                getCellStringValue(row.getCell(
                                        findColumnIndex(sheet, "Идентификатор ТУ", 1))), getSyncData(row));
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

    private static String getSyncData(Row row) {
        Sheet workSheet = row.getSheet();
        return new StringJoiner("_").
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Регион", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЭЭЛ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЭЧ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЭЧС", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЖД станция", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "ЭЧЭ/ТП/КТП", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Название ТУ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Присоединение", 2)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Размещение ПУ", 2)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Адрес ТУ", 2)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Модель ПУ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Сер. номер ПУ", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Дата монт.", 1)))).
                add(getCellStringValue(row.getCell(findColumnIndex(workSheet, "Сер. ном. УСПД", 1)))).
                toString();
    }

    private static void fillIIKData(Sheet worksheet, Map<DataType, Map<String, String>> dataMaps) {
        Row firstRow = worksheet.getRow(0);
        int lastColumnNum = firstRow.getLastCellNum();
        int enabledCount = 0;
        CellStyle commonCS = createCommonCellStyle(worksheet);
        CellStyle dateCS = createDateCellStyle(worksheet.getWorkbook());
        int iikQuantityCellNumber = findColumnIndex(worksheet, "ВСЕГО счетчиков на концентраторе", null);
        int dcCellNumber = findColumnIndex(worksheet, "Номер УСПД", null);

        for (Row row : worksheet) {
            String key = getCellStringValue(row.getCell(COUNTER_NUMBER_CELL_NUMBER));
            if (key == null) continue;
            String dcNum = getCellStringValue(row.getCell(dcCellNumber));
            if (iikCount.containsKey(dcNum)) {
                row.getCell(iikQuantityCellNumber).setCellValue(iikCount.getOrDefault(dcNum, 0));
            }

            key = key.trim();

            boolean isNormallyTurnedOff = false;
            boolean hasWrongKey = false;

            if (dataMaps.get(DataType.IIK_STATUS).containsKey(key)) {
                Cell iikStatusCell = row.createCell(IIK_STATUS_CELL_NUMBER);
                Cell connectionDateCell = row.createCell(CONNECTION_DATE_CELL_NUMBER);
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
                Cell cell = row.createCell(NORMALLY_TURNED_OFF_CELL_NUMBER);
                String normallyTurnedOff = dataMaps.get(DataType.NORMALLY_TURNED_OFF).get(key);
                cell.setCellValue(normallyTurnedOff);
                cell.setCellStyle(commonCS);
                isNormallyTurnedOff = normallyTurnedOff.equals("Да");
            }
            if (dataMaps.get(DataType.DATA_CONTROL).containsKey(key)) {
                Cell cell = row.createCell(lastColumnNum);
                Cell taskCell = row.createCell(TASK_CELL_NUMBER);
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


    private static void setTopRowProperties(Row firstRow, int lastColumnNum, int enabledCount) {
        String formattedDate = LocalDate.now().format(DATE_FORMATTER_DDMMMM);
        Cell meterStatus = firstRow.getCell(19);
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

    private static void setCellAlignment(int firstRowNum, int lastRowNum, int firstColNum, int lastColumnNum, Sheet worksheet) {
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

    private static void fillIVKEData(Sheet ivkeSheet, Map<DataType, Map<String, String>> dataMaps) {
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
                    iikQuantity.setCellValue(iikCount.getOrDefault(key, 0));
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
                    return null;
            }
        }
        return null;
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

