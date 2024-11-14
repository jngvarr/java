package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.poi.ss.format.CellFormatType.DATE;

public class ExcelMerger { // Объединение нескольких аналогичных файлов в один
    private static final Logger logger = LoggerFactory.getLogger(ExcelMerger.class);

    public static void main(String[] args) throws IOException {

//        String folderPath = "d:\\Downloads\\пто\\";
        String folderPath = "d:\\загрузки\\PTO\\План ПТО 2024\\";
        File folder = new File(folderPath);

//        String[] fileNames = new File(folderPath).list((dir, name) -> name.contains("ПТО"));

        Map<String, List<File>> fileGroups = new HashMap<>();
        fileGroups.put("ИИК", new ArrayList<>());
        fileGroups.put("ИВКЭ", new ArrayList<>());

        for (File file : Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".xlsx")))) {
            if (file.getName().contains("ИИК")) {
                fileGroups.get("ИИК").add(file);
            } else if (file.getName().contains("ИВКЭ")) {
                fileGroups.get("ИВКЭ").add(file);
            }
        }

        for (String group : fileGroups.keySet()) {
            if (!fileGroups.get(group).isEmpty()) {
                String month = extractMonthFromFileName(fileGroups.get(group).getFirst().getName());
                String year = extractYearFromFileName(fileGroups.get(group).getFirst().getName());
                String outputFileName = String.format("СВОД_%s ПТО РРЭ %s_%s.xlsx", group, year, month.toUpperCase());
                mergeExcelFiles(fileGroups.get(group), folderPath + File.separator + "свод" + File.separator + outputFileName);
            }
        }
    }

    private static String extractMonthFromFileName(String fileName) {
        Pattern pattern = Pattern.compile("(январ[ья]|феврал[ья]|март[а]?|апрел[ья]|ма[йя]|июн[ья]?|июл[ья]?|август[а]?|сентябр[ья]|октябр[ья]|ноябр[ья]|декабр[ья])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileName.toLowerCase());
        return matcher.find() ? matcher.group(1) : ""; // Возвращаем найденный месяц
    }


    private static String extractYearFromFileName(String fileName) {
        Pattern pattern = Pattern.compile("(?<=\\b|_|\\s)\\d{4}(?=\\b|_|\\s)"); // Ищем 4-значное число с гибкими границами
        Matcher matcher = pattern.matcher(fileName);
        // Находим первое совпадение
        return matcher.find() ? matcher.group() : ""; // Возвращаем найденный месяц
    }


    private static void mergeExcelFiles(List<File> inputFiles, String outputFilePath) throws IOException {
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
                newCellStyle.cloneStyleFrom(sheet.getRow(1).getCell(0).getCellStyle());
                CellStyle dateCellStyle = resultWorkbook.createCellStyle();
                short dateFormat = resultWorkbook.createDataFormat().getFormat("dd/MM/yyyy"); // Формат даты
                dateCellStyle.setDataFormat(dateFormat);


                // Копируем данные, пропуская пустые строки
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row sourceRow = sheet.getRow(i);
//                    if (sourceRow == null || isRowEmpty(sourceRow)) continue; // Пропуск пустых строк
                    if (sourceRow == null || isCellEmpty(sourceRow.getCell(0)))
                        continue; // Пропуск строки если пуста первая ячейка

                    Row targetRow = resultSheet.createRow(rowCount++);
                    copyRow(sourceRow, targetRow, columnCount, newCellStyle, dateCellStyle);
                }

                adjustColumnWidths(resultSheet, sheet, columnCount);
                applyAutoFilterAndFreezeHeader(resultSheet);
            }
        }

        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            resultWorkbook.write(fos);
        }


        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            resultWorkbook.write(fos);
        }
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
            } else {
                targetCell.setCellStyle(defaultCellStyle);
            }
        }
    }


    // Метод для проверки, пуста ли строка
    private static boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    // Метод для настройки ширины столбцов
    private static void adjustColumnWidths(Sheet resultSheet, Sheet sourceSheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            resultSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
        }
    }

    private static void applyAutoFilterAndFreezeHeader(Sheet workSheet) {
        int lastRowNum = workSheet.getLastRowNum();
        int lastColNum = workSheet.getRow(0).getLastCellNum();
        int firstColNum = 0;
        int firstRowNum = 0;
        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRowNum, lastRowNum, firstColNum, lastColNum);
        workSheet.setAutoFilter(cellRangeAddress);
        workSheet.createFreezePane(firstRowNum, firstRowNum + 1);
    }
}
