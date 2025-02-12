package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpgradedDaysDataFiller { //заполнение файла Контроль ПУ РРЭ (Задания на ОТО РРЭ)

    private static final Logger logger = LoggerFactory.getLogger(UpgradedDaysDataFiller.class);


    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_DDMMMM = DateTimeFormatter.ofPattern("dd MMMM", new Locale("ru"));

//    private static final String PLAN_OTO_PATH = "c:\\Users\\admin\\YandexDiskUKSTS\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx";
    private static final String PLAN_OTO_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx";
//    private static final String FOLDER_PATH = "d:\\Downloads\\пто\\reports\\" + LocalDate.now().format(DATE_FORMATTER_DDMMYYYY);
    private static final String FOLDER_PATH = "d:\\Downloads\\пто\\reports\\" + LocalDate.now().format(DATE_FORMATTER_DDMMYYYY);

    private enum DataType {
        DATA_CONTROL, NORMALLY_TURNED_OFF, IIK_STATUS, CONNECTION_DIAG
    }

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
            EmailSenderMultipleRecipients.main(args);


            logger.info("Data filled successfully!");

        } catch (IOException ex) {
            logger.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Execution time: " + duration / 1000 + " seconds");
    }

    private static void processFile(File file, Map<DataType, Map<String, String>>   dataMaps) {
        String fileName = file.getName();
        try {
            if (fileName.startsWith("Контроль поступления данных")) {
                dataMaps.get(DataType.DATA_CONTROL).putAll(fillingMapWithData(1, 5, file));
            } else if (fileName.startsWith("Состав ИИК")) {
                dataMaps.get(DataType.NORMALLY_TURNED_OFF).putAll(fillingMapWithData(11, 8, file));
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
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell cellKey = row.getCell(meterColumn);
                Cell cellValue = row.getCell(neededDataColumn);
                if (cellKey != null && cellValue != null) {
                    String key = getCellStringValue(cellKey);
                    String value = getCellStringValue(cellValue);
                    if (key != null && value != null) {
                        workMap.put(key.trim(), value);
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Error reading file: " + file.getName(), ex);
        }
        return workMap;
    }

    private static void fillIIKData(Sheet worksheet, Map<DataType, Map<String, String>> dataMaps) {
        Row firstRow = worksheet.getRow(0);
        int lastColumnNum = firstRow.getLastCellNum();
        int enabledCount = 0;

        for (Row row : worksheet) {
            Cell counterCell = row.getCell(13);
            String counterNumber = getCellStringValue(counterCell);
            if (counterNumber != null) {
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

