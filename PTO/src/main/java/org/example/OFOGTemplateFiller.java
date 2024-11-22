package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class OFOGTemplateFiller {

    public static void main(String[] args) throws IOException {
        // Путь к вашему шаблону
        String templatePath = "d:\\Downloads\\пто\\month_reports\\templates\\month_report_template.xlsx";
//        String dataPath = "c:\\Users\\admin\\YandexDiskUKSTS\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\ОЖ 2024.xlsx";
        String dataPath = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\ОЖ 2024.xlsx";
        // Путь для сохранения нового файла
        String outputFilePathOFlog = "d:\\Downloads\\пто\\month_reports\\filled_operational_failure_log.xlsx";
        String outputFilePathOfogIr = "d:\\Downloads\\filled_inspection_report.xlsx";
        int dateColumnIndex = 16;
        int targetMonth = LocalDate.now().getMonthValue();
        int faultReasonColNum = 18;
        int targetLogSheetRowNum = 0;
        int targetIReportRowNum = 0;


        // Открываем файл-шаблон
        try (FileInputStream templateFis = new FileInputStream(templatePath);
             XSSFWorkbook templateWorkbook = new XSSFWorkbook(templateFis)) {
            try (FileInputStream dataFis = new FileInputStream(dataPath);
                 XSSFWorkbook dataWorkbook = new XSSFWorkbook(dataFis)) {

                // Получаем первый лист
                XSSFSheet ofLogSheet = templateWorkbook.getSheet("ОФОЖ");
                XSSFSheet iReportSheet = templateWorkbook.getSheet("Акт осмотра ИИК-ИВКЭ");
                XSSFSheet dataSheet = dataWorkbook.getSheet("ОЖ");


                for (Row row : dataSheet) {
                    Cell dateCell = row.getCell(dateColumnIndex);
                    Cell faultReasonCell = row.getCell(faultReasonColNum);

                    // Проверяем, является ли ячейка датой
                    if (dateCell != null && dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                        // Получаем значение даты
                        Date cellDate = dateCell.getDateCellValue();
                        LocalDate localDate = cellDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        // Проверяем, совпадает ли месяц
                        if (localDate.getMonthValue() - 1 == cellDate.getMonth()) {

                            Row targetRow;
                            // Копируем строку в целевой лист
                            if (faultReasonCell != null && faultReasonCell.getCellType() == CellType.STRING &&
                                    !faultReasonCell.getStringCellValue().equals("Уточнение реквизитов ТУ (подана заявка на корректировку НСИ")) {
                                targetRow = ofLogSheet.createRow(targetLogSheetRowNum++ + 8);
                            } else {
                                targetRow = iReportSheet.createRow(targetIReportRowNum++ + 9);
                            }
                            copyRowsData(row, targetRow);
                        }
                    }


                }

                // Сохраняем новый файл
                try (FileOutputStream fos = new FileOutputStream(outputFilePathOFlog)) {
                    templateWorkbook.write(fos);
                }

                System.out.println("Данные успешно добавлены и сохранены в файл: " + outputFilePathOFlog);
            }
        }
    }

    private static void copyRowsData(Row sourceRow, Row targetRow) {
        // Определяем соответствие: из какой исходной колонки данные копировать и в какую целевую
        int[] sourceColumns = {15, 10, 5}; // Индексы колонок в исходной строке
        int[] targetColumns = {8, 4, 2};   // Индексы колонок в целевой строке

        for (int i = 0; i < sourceColumns.length; i++) {
            int sourceIndex = sourceColumns[i];
            int targetIndex = targetColumns[i];

            // Получаем ячейки из исходной строки и создаём соответствующую в целевой строке
            Cell sourceCell = sourceRow.getCell(sourceIndex);
            Cell targetCell = targetRow.createCell(targetIndex);

            if (sourceCell != null) {
                switch (sourceCell.getCellType()) {
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(sourceCell)) {
                            targetCell.setCellValue(sourceCell.getDateCellValue()); // Копируем дату
                        } else {
                            targetCell.setCellValue(sourceCell.getNumericCellValue()); // Копируем число
                        }
                        break;
                    case BOOLEAN:
                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        targetCell.setCellFormula(sourceCell.getCellFormula());
                        break;
                    default:
                        targetCell.setBlank(); // Оставляем ячейку пустой
                        break;
                }
            }
        }
    }

    private static void concatAndCopyRowsData(Row sourceRow, Row targetRow) {
        // Индексы колонок для объединения данных
        int[] columnsToConcat = {6, 7, 9, 13}; // Колонки, из которых нужно получить данные

        // Индекс колонки в целевой строке, куда будет записан результат
        int targetColumnIndex = 8;

        // Создаём результирующую строку
        StringBuilder concatenatedData = new StringBuilder();

        // Проходим по указанным колонкам и собираем их данные
        for (int columnIndex : columnsToConcat) {
            Cell cell = sourceRow.getCell(columnIndex);
            if (cell != null) {
                switch (cell.getCellType()) {
                    case STRING:
                        concatenatedData.append(cell.getStringCellValue()).append("/");
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            concatenatedData.append(cell.getDateCellValue().toString()).append("/");
                        } else {
                            concatenatedData.append(cell.getNumericCellValue()).append("/");
                        }
                        break;
                    case BOOLEAN:
                        concatenatedData.append(cell.getBooleanCellValue()).append("/");
                        break;
                    case FORMULA:
                        concatenatedData.append(cell.getCellFormula()).append("/");
                        break;
                    default:
                        break;
                }
            }
        }

        // Записываем объединённые данные в целевую строку
        Cell targetCell = targetRow.createCell(targetColumnIndex);
        targetCell.setCellValue(concatenatedData.toString().trim()); // Убираем лишние пробелы в конце
    }

}
