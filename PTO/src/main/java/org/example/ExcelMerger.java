package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.DataFiller.findMonthColumnIndex;

public class ExcelMerger { // Объединение нескольких аналогичных файлов в один
    private static final Logger logger = LoggerFactory.getLogger(ExcelMerger.class);
    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final LocalDate TODAY = LocalDate.now();
    private static final Map<String, DCEntry> DC = new HashMap<>();
    private static final int REGION_COL_NUMBER = 1;
    private static final int STATION_COL_NUMBER = 5;
    private static final int EEL_COL_NUMBER = 2;
    private static final int SUBSTATION_COL_NUMBER = 6;
    private static final int COUNTER_TYPE_COL_NUMBER = 9;
    private static final int SUMM_ROW_NUMBER = 12;
    private static final int IVKE_CELL_NUMBER = 5;
    private static final int METER1021_CELL_NUMBER = 7;
    private static final int METER1023_CELL_NUMBER = 8;
    private static final int METER2023_CELL_NUMBER = 9;
    private static int dcNumberColNumber;
    private static final int DC_COLUMN_NUMBER_METER_SHEET = 11;
    private static final int DC_COLUMN_NUMBER_DC_SHEET = 9;
    private static String orderMonth;
    private static String orderYear;

    private static int meter1021 = 0;
    private static int meter1023 = 0;
    private static int meter2023 = 0;
    private static int dc = 0;

    public static class DCEntry {
        private int[] counts = new int[3]; // Для типов 1021, 1023, 2023
        private String source; // Источник элемента ("ИВКЭ" или "ИИК")

        public DCEntry(String source) {
            this.source = source;
        }

        public int[] getCounts() {
            return counts;
        }

        public String getSource() {
            return source;
        }

        public void incrementCount(int index) {
            counts[index]++;
        }
    }


    public static void main(String[] args) throws IOException {

        String folderPath = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ПТО\\";
        File folder = new File(folderPath);
        String scheduleTemplatePath = "d:\\Downloads\\пто\\month_reports\\templates\\works_schedule_template.xlsx";

        Map<String, List<File>> fileGroups = new HashMap<>();
        fileGroups.put("ИИК", new ArrayList<>());
        fileGroups.put("ИВКЭ", new ArrayList<>());

        // Разделение файлов на группы
        logger.info("Разделение файлов по группам...");

        for (File file : Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".xlsx")))) {
            if (file.getName().contains("ИИК") && !file.getName().contains("СВОД")) {
                fileGroups.get("ИИК").add(file);
            } else if (file.getName().contains("ИВКЭ") && !file.getName().contains("СВОД")) {
                fileGroups.get("ИВКЭ").add(file);
            }
        }

        // Логирование групп
        fileGroups.forEach((group, files) -> logger.info("Группа '{}' содержит {} файлов", group, files.size()));
        
        ExecutorService executorService = Executors.newFixedThreadPool(2); // Пул из 2 потоков

        // Отправляем задачи в пул потоков
        List<Future<?>> futures = new ArrayList<>();
        for (String group : fileGroups.keySet()) {
            List<File> files = fileGroups.get(group);
            if (!files.isEmpty()) {
                futures.add(executorService.submit(() -> processGroup(files, folderPath, group)));
                logger.info("Начало обработки группы '{}'", group);
                logger.info("Обработка группы '{}' завершена", group);
            } else {
                logger.warn("Группа '{}' не содержит файлов для обработки", group);
            }
        }

        // Ожидаем завершения всех задач
        for (Future<?> future : futures) {
            try {
                future.get(); // Ожидание завершения задачи
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Ошибка при выполнении задачи", e);
            }
        }

        // Завершаем работу ExecutorService
        executorService.shutdown();
        logger.info("Все задачи завершены");
        fillSchedule(scheduleTemplatePath);
//        printDCEntries();
    }

    private static void fillSchedule(String scheduleTemplatePath) throws FileNotFoundException {
        try (FileInputStream templateFis = new FileInputStream(scheduleTemplatePath);
             XSSFWorkbook scheduleTemplateWorkbook = new XSSFWorkbook(templateFis)) {
            XSSFSheet scheduleSheet = scheduleTemplateWorkbook.getSheet("ГРАФИК");
            CellStyle commonCellStyle = createCommonCellStyle(scheduleSheet);
            CellStyle dateCellStyle = createDateCellStyle(scheduleSheet);
            CellStyle horizontalAlignmentCellStyle = createHorizontalAlignmentCellStyle(scheduleSheet);
            int currentRowNum = 13;
            int ordersFirstRowNum = 13;
            int firstCellOfRowNum = 0;
            String scheduleDate = "на Западно-Сибирской жд на " + orderMonth + " " + orderYear + "г.";
            System.out.println(scheduleDate);
            scheduleSheet.getRow(4).getCell(firstCellOfRowNum).setCellValue(scheduleDate);

            for (Map.Entry<String, DCEntry> dc : DC.entrySet()) {
                insertRow(scheduleSheet, dc, currentRowNum++, ordersFirstRowNum, commonCellStyle,
                        dateCellStyle, horizontalAlignmentCellStyle);
            }

            setSummRow(scheduleSheet);

            Path directoryPath = Paths.get("d:\\Downloads\\пто\\month_reports\\"
                    + orderMonth);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath); // Создает все недостающие каталоги
            }
            // Сохраняем файл
            try (FileOutputStream fos = new FileOutputStream(directoryPath + "\\График ПТО на " + orderMonth + " " + orderYear + " г..xlsx")) {
                scheduleTemplateWorkbook.write(fos);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при работе с файлом-шаблоном", e);
        }
    }

    private static void insertRow(Sheet scheduleSheet, Map.Entry<String, DCEntry> dc, int currentRowNum,
                                  int ordersFirstRowNum, CellStyle commonCellStyle,
                                  CellStyle dateCellStyle, CellStyle horizontalAlignmentCellStyle) {
        String[] placement = dc.getKey().split("_");
        String source = dc.getValue().getSource();
        int[] counters = dc.getValue().getCounts();
        int lastRowNum = scheduleSheet.getLastRowNum();
        if (currentRowNum <= lastRowNum) {
            scheduleSheet.shiftRows(currentRowNum, lastRowNum, 1);
        }
        // Создаём новую строку в таблице
        Row newRow = scheduleSheet.createRow(currentRowNum++);

        final int pointsCellNum = 0;
        final int regionCellNum = 1;
        final int eelCellNum = 2;
        final int stationCellNum = 3;
        final int substationCellNum = 4;
        final int dcNumCellNum = 5;
        final int dcDateCellNum = 6;
        final int meter1021CellNum = 7;
        final int meter1023CellNum = 8;
        final int meter2023CellNum = 9;
        final int meterDateCellNum = 10;

        for (int i = 0; i < scheduleSheet.getRow(ordersFirstRowNum - 1).getLastCellNum(); i++) {
            Cell cell = newRow.createCell(i);
            cell.setCellStyle(commonCellStyle);

            switch (i) {
                case pointsCellNum ->
                        cell.setCellValue(currentRowNum - ordersFirstRowNum);

                case regionCellNum, eelCellNum, stationCellNum, substationCellNum, dcNumCellNum ->
                        cell.setCellValue(placement[i - 1]);

                case meter1021CellNum, meter1023CellNum, meter2023CellNum -> {
                    cell.setCellValue(counters[i - 7]);
                    cell.setCellStyle(horizontalAlignmentCellStyle);
                }

                case dcDateCellNum -> {
                    if ("ИВКЭ".equalsIgnoreCase(source)) setDateCell(cell, placement[i - 1], dateCellStyle);
                }

                case meterDateCellNum -> {
                    if (Arrays.stream(counters).sum() > 0) setDateCell(cell, placement[i - 5], dateCellStyle);
                }
            }
        }
    }
    private static void setDateCell(Cell cell, String value, CellStyle style) {
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void setSummRow(Sheet scheduleSheet) {
        Row summRow = scheduleSheet.createRow(SUMM_ROW_NUMBER);
        CellStyle sumCellStyle = createSumCellStyle(scheduleSheet);

        int lastCellNum = scheduleSheet.getRow(SUMM_ROW_NUMBER + 1).getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = summRow.createCell(i);
            cell.setCellStyle(sumCellStyle);

            switch (i) {
                case IVKE_CELL_NUMBER - 1 -> cell.setCellValue("ИТОГО: ");
                case IVKE_CELL_NUMBER -> cell.setCellValue(dc);
                case METER1021_CELL_NUMBER -> cell.setCellValue(meter1021);
                case METER1023_CELL_NUMBER -> cell.setCellValue(meter1023);
                case METER2023_CELL_NUMBER -> cell.setCellValue(meter2023);
            }
        }
    }

    private static CellStyle createSumCellStyle(Sheet scheduleSheet) {
        Workbook workbook = scheduleSheet.getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();

        // Пример настроек стиля
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);

        return cellStyle;
    }

    private static void processGroup(List<File> files, String folderPath, String group) {
        try {
            orderMonth = extractMonthFromFileName(files.get(0).getName());
            orderYear = extractYearFromFileName(files.get(0).getName());
            String outputFileName = String.format("СВОД_%s ПТО РРЭ %s_%s.xlsx", group, orderYear, orderMonth.toUpperCase());
            mergeExcelFiles(files, folderPath + File.separator + File.separator + outputFileName);
        } catch (IOException e) {
            logger.error("Ошибка при обработке группы '{}'", group, e);
        }
    }


    private static String extractMonthFromFileName(String fileName) {
        logger.debug("Извлечение месяца из имени файла '{}'", fileName);
        Pattern pattern = Pattern.compile("(январ[ья]|феврал[ья]|март[а]?|апрел[ья]|ма[йя]|июн[ья]?|июл[ья]?|август[а]?|сентябр[ья]|октябр[ья]|ноябр[ья]|декабр[ья])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileName.toLowerCase());
        return matcher.find() ? matcher.group(1) : ""; // Возвращаем найденный месяц
    }


    private static String extractYearFromFileName(String fileName) {
        logger.debug("Извлечение года из имени файла '{}'", fileName);
        Pattern pattern = Pattern.compile("(?<=\\b|_|\\s)\\d{4}(?=\\b|_|\\s)"); // Ищем 4-значное число с гибкими границами
        Matcher matcher = pattern.matcher(fileName);
        // Находим первое совпадение
        return matcher.find() ? matcher.group() : ""; // Возвращаем найденный месяц
    }


    private static void mergeExcelFiles(List<File> inputFiles, String outputFilePath) throws IOException {
        logger.info("Создание итогового файла '{}'", outputFilePath);
        File outputFolder = new File(outputFilePath).getParentFile();
        if (!outputFolder.exists()) {
            outputFolder.mkdirs(); // Создание папки, если её нет
        }

        XSSFWorkbook resultWorkbook = new XSSFWorkbook();
        XSSFSheet resultSheet = resultWorkbook.createSheet("Свод");

        int rowCount = 0;
        boolean headerCopied = false;

        for (File file : inputFiles) {
            try (FileInputStream fis = new FileInputStream(file);
                 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
                XSSFSheet sheet = workbook.getSheetAt(0);

                int columnCount = sheet.getRow(0).getLastCellNum();

                // Копируем заголовок только один раз
                if (!headerCopied) {
                    copyHeader(resultSheet, sheet.getRow(0), rowCount, columnCount);
                    rowCount++;
                    headerCopied = true;
                }

                CellStyle newCellStyle = resultWorkbook.createCellStyle();
                Row sampleRow = sheet.getRow(1);
                if (sampleRow != null && sampleRow.getCell(0) != null) {
                    newCellStyle.cloneStyleFrom(sampleRow.getCell(0).getCellStyle());
                }
                CellStyle dateCellStyle = createDateCellStyle(resultWorkbook);


                // Копируем данные, пропуская пустые строки
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row sourceRow = sheet.getRow(i);
//                    if (sourceRow == null || isRowEmpty(sourceRow)) continue; // Пропуск пустых строк
                    if (sourceRow == null || isCellEmpty(sourceRow.getCell(1)))
                        continue; // Пропуск строки если пуста первая ячейка

                    Row targetRow = resultSheet.createRow(rowCount++);
                    copyRow(sourceRow, targetRow, columnCount, newCellStyle, dateCellStyle);
                }
                adjustColumnWidths(resultSheet, sheet, columnCount);
                applyAutoFilterAndFreezeHeader(resultSheet);
            } catch (IOException e) {
                logger.error("Ошибка при обработке файла: " + file.getName(), e);
            }
        }

        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            resultWorkbook.write(fos);
        }
        setMonthSchedule(outputFilePath, resultSheet);

    }

    private static CellStyle createCommonCellStyle(Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();

        // Пример настроек стиля
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        return cellStyle;
    }

    private static CellStyle createDateCellStyle(Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        DataFormat poiDataFormat = workbook.createDataFormat();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(poiDataFormat.getFormat("dd.MM.yyyy"));

        dateCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setBorderTop(BorderStyle.THIN);
        dateCellStyle.setBorderLeft(BorderStyle.THIN);
        dateCellStyle.setBorderRight(BorderStyle.THIN);
        return dateCellStyle;
    }

    private static CellStyle createHorizontalAlignmentCellStyle(Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();

        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }


    private static void printDCEntries() {
        System.out.println("Содержимое DC:");
        for (Map.Entry<String, DCEntry> entry : DC.entrySet()) {
            String key = entry.getKey();
            DCEntry value = entry.getValue();
            System.out.printf("Key: %s, Source: %s, Counts: %s%n",
                    key,
                    value.getSource(),
                    Arrays.toString(value.getCounts()));
        }
    }

    private static void setMonthSchedule(String path, Sheet resultSheet) {
        String monthFromFileName = extractMonthFromFileName(path.toLowerCase());
        String source = path.contains("ИВКЭ") ? "ИВКЭ" : "ИИК";
        dcNumberColNumber = path.contains("ИВКЭ") ? DC_COLUMN_NUMBER_DC_SHEET : DC_COLUMN_NUMBER_METER_SHEET;
        int monthColumnIndex = findMonthColumnIndex(resultSheet, monthFromFileName);
        if (monthColumnIndex == -1) {
            System.out.println("Month column not found.");
            return;
        }

        for (int i = 1; i <= resultSheet.getLastRowNum(); i++) {
            Row targetRow = resultSheet.getRow(i);
            Cell targetRowCell = targetRow.getCell(monthColumnIndex);
            if (getCellStringValue(targetRowCell) != null && !getCellStringValue(targetRowCell).isEmpty()) {
                String counterType = targetRow.getCell(COUNTER_TYPE_COL_NUMBER).getStringCellValue();
                String key = setKey(targetRow, monthColumnIndex);

                if (path.contains("ИВКЭ")) dc++;

                DC.putIfAbsent(key, new DCEntry(source));
                DCEntry entry = DC.get(key);

                if (counterType.contains("1021")) {
                    entry.incrementCount(0);
                    meter1021++;
                } else if (counterType.contains("1023")) {
                    entry.incrementCount(1);
                    meter1023++;
                } else if (counterType.contains("2023")) {
                    entry.incrementCount(2);
                    meter2023++;
                }

            }
        }
    }

    private static String setKey(Row row, int monthColumnIndex) {
        return new StringBuilder()
                .append(row.getCell(REGION_COL_NUMBER).getStringCellValue())
                .append("_")
                .append(row.getCell(EEL_COL_NUMBER).getStringCellValue())
                .append("_")
                .append(row.getCell(STATION_COL_NUMBER).getStringCellValue())
                .append("_")
                .append(row.getCell(SUBSTATION_COL_NUMBER).getStringCellValue())
                .append("_")
                .append(getCellStringValue(row.getCell(dcNumberColNumber)))
                .append("_")
                .append(getCellStringValue(row.getCell(monthColumnIndex)))
                .toString();
    }

    private static boolean isCellEmpty(Cell cell) {
        return (cell == null || cell.getCellType() == CellType.BLANK || (cell.getCellType() == CellType.STRING
                && cell.getStringCellValue().trim().isEmpty()));
    }

    private static void copyHeader(Sheet resultSheet, Row headerRow, int rowIndex, int columnCount) {
        Row targetHeaderRow = resultSheet.createRow(rowIndex);
        Workbook resultWorkbook = resultSheet.getWorkbook();

        for (int i = 0; i < columnCount; i++) {
            Cell sourceCell = headerRow.getCell(i);
            Cell targetCell = targetHeaderRow.createCell(i);

            if (sourceCell != null) {
                targetCell.setCellValue(sourceCell.toString());

                // Копируем стиль заголовка
                CellStyle newCellStyle = resultWorkbook.createCellStyle();
                newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                targetCell.setCellStyle(newCellStyle);
            }
        }
    }


    // Метод для копирования данных строки
    private static void copyRow(Row sourceRow, Row targetRow, int columnCount, CellStyle defaultCellStyle, CellStyle dateCellStyle) {
        for (int i = 0; i < columnCount; i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);

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

    // Метод для настройки ширины столбцов
    private static void adjustColumnWidths(Sheet resultSheet, Sheet sourceSheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            resultSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
        }
    }

    private static CellStyle createDateCellStyle(Workbook resultWorkbook) {
        CellStyle dateCellStyle = resultWorkbook.createCellStyle();
        DataFormat dateFormat = resultWorkbook.createDataFormat(); // Формат даты
        dateCellStyle.setDataFormat(dateFormat.getFormat("dd.MM.yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        return dateCellStyle;
    }

    private static void applyAutoFilterAndFreezeHeader(Sheet workSheet) {
        int lastRowNum = workSheet.getLastRowNum();
        int lastColNum = workSheet.getRow(0).getLastCellNum();
        int firstColNum = 0;
        int firstRowNum = 0;
        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRowNum, lastRowNum - 1, firstColNum, lastColNum - 1);
        workSheet.setAutoFilter(cellRangeAddress);
        workSheet.createFreezePane(firstRowNum, firstRowNum + 1);
    }

    private static String getCellStringValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    // Check if it's a date or numeric value
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // Format date to dd.MM.yyyy
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        return dateFormat.format(cell.getDateCellValue());
                    } else {
                        return new DecimalFormat("0").format(cell.getNumericCellValue()); // Convert numeric to string
                    }
                case FORMULA:
                    return String.valueOf(cell.getCellFormula());
                default:
                    return null;
            }
        }
        return null;
    }
}
