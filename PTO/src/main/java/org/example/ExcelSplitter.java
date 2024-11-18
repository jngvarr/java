package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelSplitter {

    private static final Logger logger = LoggerFactory.getLogger(ExcelSplitter.class);

    public static void main(String[] args) throws IOException {

        // Путь к папке "свод"
        String inputFilePath = "d:\\Downloads\\пто\\свод\\"; // дома
//            String inputFilePath = "d:\\загрузки\\PTO\\План ПТО 2024\\свод";
        File inputFolder = new File(inputFilePath);

        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            System.err.println("Указанная папка не существует или не является директорией: " + inputFilePath);
            return;
        }

        // Получаем только файлы, содержащие "ПТО" в названии, из папки "свод"
        File[] fileNames = inputFolder.listFiles(file -> file.isFile() && file.getName().contains("ПТО"));

        if (fileNames == null || fileNames.length == 0) {
            System.err.println("Нет файлов для обработки в папке: " + inputFilePath);
            return;
        }

        String outputFolderPath = "d:\\Downloads\\пто\\";                        //дома
//            String outputFolderPath = "d:\\загрузки\\PTO\\План ПТО 2024\\разделенные";
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // Обработка каждого файла
        for (File file : fileNames) {
            System.out.println("Обработка файла: " + file.getAbsolutePath());
            splitExcelFile(file, outputFolderPath);
        }
    }


    private static void splitExcelFile(File inputFilePath, String outputFolderPath) throws IOException {

        String month = extractMonthFromFileName(inputFilePath.toString());
        String year = extractYearFromFileName(inputFilePath.getName());

        try (FileInputStream fis = new FileInputStream(inputFilePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            Map<String, List<Row>> groupedRows = new HashMap<>();

            // Найти индекс столбца "НТЭЛ"
            int ntelColumnIndex = findColumnIndex(sheet, "НТЭЛ");
            if (ntelColumnIndex == -1) {
                logger.error("Столбец 'НТЭЛ' не найден");
                return;
            }

            // Группируем строки по значениям в столбце "НТЭЛ"
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Пропускаем заголовок
                Row row = sheet.getRow(i);
                if (row == null || isCellEmpty(row.getCell(0))) continue;

                Cell cell = row.getCell(ntelColumnIndex);
                String ntelValue = cell != null ? cell.toString() : "Без НТЭЛ";

                groupedRows.computeIfAbsent(ntelValue, k -> new ArrayList<>()).add(row);
            }

            // Создаем файлы для каждой группы
            for (Map.Entry<String, List<Row>> entry : groupedRows.entrySet()) {
                String ntelValue = entry.getKey();
                List<Row> rows = entry.getValue();

                String outputFileName = ntelValue.replaceAll("[^a-zA-Zа-яА-Я0-9]", "_")
                        + (inputFilePath.toString().contains("ИИК") ? "_ИИК" : "_ИВКЭ") + "_ПТО РРЭ " + year + "_"
                        + month.toUpperCase() + ".xlsx";
                String outputFilePath = outputFolderPath + File.separator + outputFileName;

                createExcelFile(outputFilePath, sheet.getRow(0), rows);
                logger.info("Создан файл: {}", outputFilePath);
            }
        }
    }

    private static int findColumnIndex(Sheet sheet, String columnName) {
        Row headerRow = sheet.getRow(0); // Заголовок на первой строке
        if (headerRow == null) return -1;

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && columnName.equalsIgnoreCase(cell.getStringCellValue())) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCellEmpty(Cell cell) {  //проверяем строку на пустоту по первой ячейке, которая всегда заполнена
        return (cell == null || cell.getCellType() == CellType.BLANK || (cell.getCellType() == CellType.STRING
                && cell.getStringCellValue().trim().isEmpty()));
    }

    private static void createExcelFile(String outputFilePath, Row headerRow, List<Row> rows) throws IOException {
        boolean isHeader = true;
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {

            XSSFSheet sheet = workbook.createSheet("Разделенный");
            int rowCount = 0;

            // Копируем заголовок
            Row targetHeaderRow = sheet.createRow(rowCount++);
            copyRow(headerRow, targetHeaderRow, isHeader, null);
            isHeader = false;


            CellStyle simpleCellStyle = workbook.createCellStyle();
            Row sampleRow = rows.get(1);
            if (sampleRow != null && sampleRow.getCell(0) != null) {
                simpleCellStyle.cloneStyleFrom(sampleRow.getCell(0).getCellStyle());
            }
//            CellStyle dateCellStyle = createDateCellStyle(resultWorkbook);


            // Копируем строки
            for (Row row : rows) {
                Row targetRow = sheet.createRow(rowCount++);
                copyRow(row, targetRow, isHeader, simpleCellStyle);
            }

            // Настройка ширины столбцов
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.setColumnWidth(i, sheet.getColumnWidth(i));
            }

            workbook.write(fos);
        }
    }

    private static void copyRow(Row sourceRow, Row targetRow, boolean isHeader, CellStyle simpleCellStyle) {
        Sheet resultSheet = targetRow.getSheet();
        Sheet sourceSheet = sourceRow.getSheet();

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);

            if (sourceCell != null) {
                switch (sourceCell.getCellType()) {
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(sourceCell)) {
                            targetCell.setCellValue(sourceCell.getDateCellValue());
                        } else {
                            targetCell.setCellValue(sourceCell.getNumericCellValue());
                        }
                        break;
                    case BOOLEAN:
                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        targetCell.setCellFormula(sourceCell.getCellFormula());
                        break;
                    default:
                        break;
                }
                if (isHeader) {
                    CellStyle targetCellStyle = targetCell.getSheet().getWorkbook().createCellStyle();
                    targetCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                    targetCell.setCellStyle(targetCellStyle);
                    resultSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));

                } else {
                    targetCell.setCellStyle(simpleCellStyle);

                }
            }
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
}
