package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelMerger {
    public static void main(String[] args) throws IOException {
        String folderPath = "d:\\Downloads\\пто\\";
        String[] fileNames = new File(folderPath).list((dir, name) -> name.endsWith(".xlsx"));

        if (fileNames == null) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheetIIK = workbook.createSheet("Объединенные ИИК");
            XSSFSheet sheetIVKE = workbook.createSheet("Объединенные ИВКЭ");

            boolean headersAddedIIK = false;
            boolean headersAddedIVKE = false;

            for (String fileName : fileNames) {
                File file = new File(folderPath + fileName);
                try (FileInputStream fis = new FileInputStream(file);
                     XSSFWorkbook inputWorkbook = new XSSFWorkbook(fis)) {

                    Sheet inputSheet = inputWorkbook.getSheetAt(0);
                    Iterator<Row> rowIterator = inputSheet.iterator();

                    XSSFSheet targetSheet;
                    boolean headersAdded;

                    if (fileName.contains("ИИК")) {
                        targetSheet = sheetIIK;
                        headersAdded = headersAddedIIK;
                        headersAddedIIK = true;
                    } else if (fileName.contains("ИВКЭ")) {
                        targetSheet = sheetIVKE;
                        headersAdded = headersAddedIVKE;
                        headersAddedIVKE = true;
                    } else {
                        continue;
                    }

                    int lastRowNum = targetSheet.getLastRowNum() + 1;

                    // Копируем строки (включая заголовок только из первого файла соответствующего типа)
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        // Пропускаем заголовок, если он уже был добавлен
                        if (row.getRowNum() == 0 && headersAdded) {
                            continue;
                        }

                        // Создаем новую строку в целевом листе
                        Row newRow = targetSheet.createRow(lastRowNum++);

                        // Копируем ячейки
                        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                            Cell cell = row.getCell(i);
                            Cell newCell = newRow.createCell(i);

                            if (cell != null) {
                                // Копируем тип ячейки и значение
                                newCell.setCellType(cell.getCellType());
                                switch (cell.getCellType()) {
                                    case STRING:
                                        newCell.setCellValue(cell.getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        newCell.setCellValue(cell.getNumericCellValue());
                                        break;
                                    case BOOLEAN:
                                        newCell.setCellValue(cell.getBooleanCellValue());
                                        break;
                                    case FORMULA:
                                        newCell.setCellFormula(cell.getCellFormula());
                                        break;
                                    // Добавьте дополнительные типы по необходимости
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Сохраняем объединенные данные в новый файл
            try (FileOutputStream fos = new FileOutputStream(folderPath + "Объединенные_данные.xlsx")) {
                workbook.write(fos);
            }

            System.out.println("Объединение завершено!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

