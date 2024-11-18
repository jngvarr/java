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

public class ExcelSplitter {

    private static final Logger logger = LoggerFactory.getLogger(ExcelSplitter.class);

    public static void main(String[] args) throws IOException {

//        String inputFilePath = "d:\\загрузки\\PTO\\План ПТО 2024\\свод";
//        File[] inputFolder = new File(inputFilePath).listFiles((dir, name) -> name.contains("ПТО"));
//        String outputFolderPath = "d:\\загрузки\\PTO\\План ПТО 2024\\";
//
//        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
//            System.err.println("Указанная папка не существует или не является директорией: " + inputFilePath);
//            return;
//        }
//
//        // Получаем только файлы, содержащие "ПТО" в названии, из папки "свод"
//        File[] fileNames = inputFolder.listFiles(file -> file.isFile() && file.getName().contains("ПТО"));
//
//        if (fileNames == null || fileNames.length == 0) {
//            System.err.println("Нет файлов для обработки в папке: " + inputFilePath);
//            return;
//        }
//
//        String outputFolderPath = "d:\\загрузки\\PTO\\План ПТО 2024\\разделенные";
//        File outputFolder = new File(outputFolderPath);
//        if (!outputFolder.exists()) {
//            outputFolder.mkdirs();
//        }
//
//        // Обработка каждого файла
//        for (File file : fileNames) {
//            System.out.println("Обработка файла: " + file.getAbsolutePath());
//            splitExcelFile(file, outputFolderPath);
//        }


            // Путь к папке "свод"
            String inputFilePath = "d:\\загрузки\\PTO\\План ПТО 2024\\свод";
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

            String outputFolderPath = "d:\\загрузки\\PTO\\План ПТО 2024\\разделенные";
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
                if (row == null || isRowEmpty(row)) continue;

                Cell cell = row.getCell(ntelColumnIndex);
                String ntelValue = cell != null ? cell.toString() : "Без НТЭЛ";

                groupedRows.computeIfAbsent(ntelValue, k -> new ArrayList<>()).add(row);
            }

            // Создаем файлы для каждой группы
            for (Map.Entry<String, List<Row>> entry : groupedRows.entrySet()) {
                String ntelValue = entry.getKey();
                List<Row> rows = entry.getValue();

                String outputFileName = ntelValue.replaceAll("[^a-zA-Zа-яА-Я0-9]", "_") + ".xlsx";
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

    private static void createExcelFile(String outputFilePath, Row headerRow, List<Row> rows) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {

            XSSFSheet sheet = workbook.createSheet("Разделенный");
            int rowCount = 0;

            // Копируем заголовок
            Row targetHeaderRow = sheet.createRow(rowCount++);
            copyRow(headerRow, targetHeaderRow);

            // Копируем строки
            for (Row row : rows) {
                Row targetRow = sheet.createRow(rowCount++);
                copyRow(row, targetRow);
            }

            // Настройка ширины столбцов
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.setColumnWidth(i, sheet.getColumnWidth(i));
            }

            workbook.write(fos);
        }
    }

    private static void copyRow(Row sourceRow, Row targetRow) {
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
                targetCell.setCellStyle(sourceCell.getCellStyle());
            }
        }
    }
}
