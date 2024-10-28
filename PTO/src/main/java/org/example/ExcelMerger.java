package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExcelMerger {
    public static void main(String[] args) {
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

                    // Сохраняем ширину столбцов
                    copyColumnWidths(inputSheet, targetSheet);

                    int lastRowNum = targetSheet.getLastRowNum() + 1;

                    // Копируем строки и стили
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        // Пропускаем заголовок, если он уже был добавлен
                        if (row.getRowNum() == 0 && headersAdded) {
                            continue;
                        }

                        // Создаем новую строку в целевом листе
                        Row newRow = targetSheet.createRow(lastRowNum++);

                        copyRowWithStyles(inputSheet, targetSheet, row, newRow);
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

    private static void copyColumnWidths(Sheet sourceSheet, Sheet targetSheet) {
        for (int i = 0; i < sourceSheet.getRow(0).getLastCellNum(); i++) {
            targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
        }
    }

    private static void copyRowWithStyles(Sheet sourceSheet, Sheet targetSheet, Row sourceRow, Row targetRow) {
        Map<Integer, CellStyle> styleMap = new HashMap<>();

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = targetRow.createCell(i);

            if (oldCell != null) {
                if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
                    newCell.setCellStyle(oldCell.getCellStyle());
                } else {
                    // Копируем стиль, если исходный и целевой workbook разные
                    CellStyle newCellStyle = styleMap.get(oldCell.getCellStyle().hashCode());
                    if (newCellStyle == null) {
                        newCellStyle = targetSheet.getWorkbook().createCellStyle();
                        newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                        styleMap.put(oldCell.getCellStyle().hashCode(), newCellStyle);
                    }
                    newCell.setCellStyle(newCellStyle);
                }

                // Копируем тип ячейки и значение
                switch (oldCell.getCellType()) {
                    case STRING:
                        newCell.setCellValue(oldCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        newCell.setCellValue(oldCell.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        newCell.setCellValue(oldCell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        newCell.setCellFormula(oldCell.getCellFormula());
                        break;
                    case BLANK:
                        newCell.setBlank();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
