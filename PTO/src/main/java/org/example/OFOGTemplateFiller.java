package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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
        String dataPath = "c:\\Users\\admin\\YandexDiskUKSTS\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\ОЖ 2024.xlsx";
        // Путь для сохранения нового файла
        String outputFilePathOFlog = "d:\\загрузки\\filled_operational_failure_log.xlsx";
        String outputFilePathOfogIr = "d:\\загрузки\\filled_inspection_report.xlsx";
        int dateColumnIndex = 16;
        int targetMonth = LocalDate.now().getMonthValue();
        int причинаНеисправности = 18;
        int targetRowNum = 0;


        // Открываем файл-шаблон
        try (FileInputStream templateFis = new FileInputStream(templatePath);
             XSSFWorkbook templateWorkbook = new XSSFWorkbook(templateFis)) {
            try (FileInputStream dataFis = new FileInputStream(templatePath);
                 XSSFWorkbook dataWorkbook = new XSSFWorkbook(dataFis)) {

                // Получаем первый лист
                XSSFSheet ofLogSheet = templateWorkbook.getSheet("ОФОЖ");
                XSSFSheet iReport = templateWorkbook.getSheet("Акт осмотра ИИК-ИВКЭ");
                XSSFSheet dataSheet = templateWorkbook.getSheet("ОЖ");


                for (Row row : dataSheet) {
                    Cell dateCell = row.getCell(dateColumnIndex);

                    // Проверяем, является ли ячейка датой
                    if (dateCell != null && dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                        // Получаем значение даты
                        Date cellDate = dateCell.getDateCellValue();
                        LocalDate localDate = cellDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        // Проверяем, совпадает ли месяц
                        if (localDate.getMonthValue() == cellDate.getMonth()) {
                            // Копируем строку в целевой лист
                            Row targetRow = dataSheet.createRow(targetRowNum++);
                            copyRow(row, targetRow);
                        }
                    }


                }

                // Сохраняем новый файл
                try (FileOutputStream fos = new FileOutputStream(outputFilePathOfog)) {
                    templateWorkbook.write(fos);
                }

                System.out.println("Данные успешно добавлены и сохранены в файл: " + outputFilePathOfog);
            }
        }
    }

    private static void copyRow(Row sourceRow, Row targetRow, boolean isHeader, CellStyle simpleCellStyle, CellStyle dateCellStyle) {
        Sheet resultSheet = targetRow.getSheet();
        Sheet sourceSheet = sourceRow.getSheet();

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);

            if (sourceCell != null) {
                switch (sourceCell.getCellType()) {
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        targetCell.setCellStyle(simpleCellStyle);
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(sourceCell)) {
                            targetCell.setCellValue(sourceCell.getDateCellValue()); // Копируем дату
                            targetCell.setCellStyle(dateCellStyle); // Применяем стиль для даты
                        } else {
                            targetCell.setCellValue(sourceCell.getNumericCellValue()); // Копируем число
                            targetCell.setCellStyle(simpleCellStyle);
                        }
                        break;
                    case BOOLEAN:
                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                        targetCell.setCellStyle(simpleCellStyle);
                        break;
                    case FORMULA:
                        targetCell.setCellFormula(sourceCell.getCellFormula());
                        targetCell.setCellStyle(simpleCellStyle);
                        break;
                    default:
                        break;
                }
                if (isHeader) {
                    CellStyle targetCellStyle = targetCell.getSheet().getWorkbook().createCellStyle();
                    targetCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                    targetCell.setCellStyle(targetCellStyle);
                    resultSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
                }
            }
        }
}
