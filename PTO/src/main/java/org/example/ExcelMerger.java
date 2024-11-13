package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelMerger {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String folderPath = "d:\\Downloads\\пто\\";

        String[] fileNames = new File(folderPath).list((dir, name) -> name.contains("ПТО"));
        if (fileNames == null) return;

        String month = extractMonthFromFileName(fileNames[0]);
        String year = extractYearFromFileName(fileNames[0]);

        // Создаем книги для ИИК и ИВКЭ
        XSSFWorkbook workbookIIK = new XSSFWorkbook();
        XSSFWorkbook workbookIVKE = new XSSFWorkbook();

        String outputFileIIK = "d:\\Downloads\\пто\\свод\\СВОД_ИИК_ПТО_РРЭ_" + month + "_" + year + ".xlsx";
        String outputFileIVKE = "d:\\Downloads\\пто\\свод\\СВОД_ИВКЭ_ПТО_РРЭ_" + month + "_" + year + ".xlsx";

        try {
            boolean headersAddedIIK = false;
            boolean headersAddedIVKE = false;
            for (String fileName : fileNames) {
                File file = new File(folderPath + fileName);
                try (FileInputStream fis = new FileInputStream(file);
                     XSSFWorkbook inputWorkbook = new XSSFWorkbook(fis)) {

                    Sheet inputSheet = inputWorkbook.getSheetAt(0);
                    Iterator<Row> rowIterator = inputSheet.iterator();

                    XSSFWorkbook targetWorkbook;
                    Sheet targetSheet;
                    boolean headersAdded;

                    if (fileName.contains("ИИК")) {
                        targetWorkbook = workbookIIK;
                        targetSheet = getOrCreateSheet(targetWorkbook, "Свод ИИК");
                        headersAdded = headersAddedIIK;
                        headersAddedIIK = true;
                    } else if (fileName.contains("ИВКЭ")) {
                        targetWorkbook = workbookIVKE;
                        targetSheet = getOrCreateSheet(targetWorkbook, "Свод ИВКЭ");
                        headersAdded = headersAddedIVKE;
                        headersAddedIVKE = true;
                    } else {
                        continue;
                    }

                    int lastRowNum = targetSheet.getLastRowNum() + 1;

                    // Копируем строки, включая заголовок только для первого файла соответствующего типа
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        if (!headersAdded && row.getRowNum() == 0) {
                            copyRowWithStyles(row, targetSheet.createRow(lastRowNum++), targetWorkbook);
                        } else if (row.getRowNum() > 0) {
                            copyRowWithStyles(row, targetSheet.createRow(lastRowNum++), targetWorkbook);
                        }
                    }
                }
            }

            // Сохранение итоговых книг
            try (FileOutputStream fosIIK = new FileOutputStream(outputFileIIK);
                 FileOutputStream fosIVKE = new FileOutputStream(outputFileIVKE)) {
                workbookIIK.write(fosIIK);
                workbookIVKE.write(fosIVKE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbookIIK.close();
                workbookIVKE.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Время выполнения: " + duration / 1000 + " секунд(ы)");
    }

    private static Sheet getOrCreateSheet(XSSFWorkbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet == null ? workbook.createSheet(sheetName) : sheet;
    }

    private static CellStyle getOrCreateCellStyle(Cell sourceCell, XSSFWorkbook targetWorkbook) {
        // Находим существующий стиль
        for (int i = 0; i < targetWorkbook.getNumCellStyles(); i++) {
            CellStyle style = targetWorkbook.getCellStyleAt((short) i); // Приведение к short
            if (style.equals(sourceCell.getCellStyle())) {
                return style;
            }
        }
        // Если стиль не найден, создаем новый
        CellStyle newCellStyle = targetWorkbook.createCellStyle();
        newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
        return newCellStyle;
    }

    private static void copyRowWithStyles(Row sourceRow, Row targetRow, XSSFWorkbook targetWorkbook) {
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);
            if (sourceCell != null) {
                // Получаем или создаем стиль
                CellStyle style = getOrCreateCellStyle(sourceCell, targetWorkbook);
                targetCell.setCellStyle(style);

                // Копируем данные
                switch (sourceCell.getCellType()) {
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        targetCell.setCellValue(sourceCell.getNumericCellValue());
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
            }
        }
    }

    // Метод для извлечения месяца из имени файла
    private static String extractMonthFromFileName(String fileName) {
        Pattern pattern = Pattern.compile("\\b(январ[ья]|феврал[ья]|март[а]?|апрел[я]?|ма[я]?|июн[ья]?|июл[ья]?|август[а]?|сентябр[я]?|октябр[я]?|ноябр[я]?|декабр[я]?)\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileName);
        return matcher.find() ? matcher.group(1) : "";
    }

    // Метод для извлечения года из имени файла
    private static String extractYearFromFileName(String fileName) {
        Pattern pattern = Pattern.compile("(?<=\\b|_|\\s)\\d{4}(?=\\b|_|\\s)"); // Находит 4-значные числа
//        Pattern pattern = Pattern.compile("\\b(\\d{4})\\b"); // Находит 4-значные числа
        Matcher matcher = pattern.matcher(fileName);
        return matcher.find() ? matcher.group(1) : "";
    }
}


//напиши код на java для объединения нескольких файлов excel:
//        1. Все файлы лежат в одной папки,
//2. файлы содержащие в названии "ИИК" должны быть собраны в файле СВОД_ИИК ПТО РРЭ <ГОД>_<МЕСЯЦ>, год и месяц нужно заменить, взяв из имени любого из исходных файлов
//3. файлы содержащие в названии "ИВКЭ" должны быть собраны в файле СВОД_ИВКЭ ПТО РРЭ <ГОД>_<МЕСЯЦ>, год и месяц нужно заменить, взяв из имени любого из исходных файлов
//4. Ширина столбцов итоговых файлов должна остаться, как у объединяемых файлов
//5. Ячейки заголовка в итоговом файле должен сохранить стиль исходных файлов
//6. В итоговых данных не должно быть пустых строк
