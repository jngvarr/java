package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class OFOGTemplateFiller {
    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Logger logger = LoggerFactory.getLogger(OFOGTemplateFiller.class);

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
        int targetLogSheetColNum = 0;
        int targetIReportColNum = 0;
        int reportRows = 0;


        // Открываем файл-шаблон
        try (FileInputStream templateFis = new FileInputStream(templatePath);
             XSSFWorkbook templateWorkbook = new XSSFWorkbook(templateFis)) {
            try (FileInputStream dataFis = new FileInputStream(dataPath);
                 XSSFWorkbook dataWorkbook = new XSSFWorkbook(dataFis)) {

                // Получаем первый лист
                XSSFSheet ofLogSheet = templateWorkbook.getSheet("ОФОЖ");
                XSSFSheet iReportSheet = templateWorkbook.getSheet("Акт осмотра ИИК-ИВКЭ");
                XSSFSheet dataSheet = dataWorkbook.getSheet("ОЖ");
                CellStyle dateCellStyle = createDateCellStyle(ofLogSheet.getWorkbook());
                CellStyle commonCellStyle = createCommonCellStyle(ofLogSheet);

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
                        int year = LocalDate.now().getYear();
                        int month = LocalDate.now().getMonthValue();

                        int eventYear = localDate.getYear();
                        int eventMonth = localDate.getMonthValue();
                        logger.info("Дата в строке: день {}, месяц {}, год {}", localDate.getDayOfMonth(), eventMonth, eventYear);
                        // Проверяем, совпадает ли месяц
                        logger.info("Выполнение условия {}:", month == eventMonth && year == eventYear);
                        if (month == eventMonth && year == eventYear) {
                            logger.info("Содержание строки №{}, {}", row.getRowNum(), faultReasonCell.getStringCellValue());
                            reportRows++;
                            Row targetRow;
                            // Копируем строку в целевой лист
                            if (faultReasonCell != null && faultReasonCell.getCellType() == CellType.STRING &&
                                    !faultReasonCell.getStringCellValue().equals("Уточнение реквизитов ТУ (подана заявка на корректировку НСИ")) {
                                targetRow = ofLogSheet.createRow(targetLogSheetColNum++ + 8);
                            } else {
                                targetRow = iReportSheet.createRow(targetIReportColNum++ + 9);
                            }
                            copyRowsData(row, targetRow, commonCellStyle, dateCellStyle);
                        }
                    }


                }

                // Сохраняем новый файл
                try (FileOutputStream fos = new FileOutputStream(outputFilePathOFlog)) {
                    templateWorkbook.write(fos);
                }
                try (FileOutputStream fos2 = new FileOutputStream(outputFilePathOfogIr)) {
                    templateWorkbook.write(fos2);
                }

                System.out.println("Данные успешно добавлены и сохранены в файл: " + outputFilePathOFlog);

            } catch (IOException e) {
                logger.error("Ошибка при работе с данными файла: {}", e.getMessage());
                e.printStackTrace();
            }
        } catch (
                FileNotFoundException e) {
            logger.error("Файл не найден: {}", e.getMessage());
        } catch (
                IOException e) {
            logger.error("Ошибка чтения/записи файла: {}", e.getMessage());
            e.printStackTrace();

        }
        logger.info("Совпавших строк в отчете {}:", reportRows);
    }

    private static void copyRowsData(Row sourceRow, Row targetRow, CellStyle commonCellStyle, CellStyle dateCellStyle) {
        concatAndCopyRowsData(sourceRow, targetRow);
        copySpecificCells(sourceRow, targetRow, commonCellStyle, dateCellStyle);
    }

    private static void copySpecificCells(Row sourceRow, Row targetRow, CellStyle commonCellStyle, CellStyle dateCellStyle) {
        String[] dates = getReportDates(sourceRow.getCell(16));
        targetRow.createCell(8).setCellValue(dates[3]);
        targetRow.createCell(9).setCellValue(dates[4]);

        for (int i = 0; i < dates.length - 1; i++) {
            targetRow.createCell(i).setCellValue(dates[i]);
        }
        targetRow.createCell(3).setCellValue("ЗСЖД");
        copyCellData(sourceRow.getCell(10), targetRow.createCell(4), commonCellStyle);
        copyCellData(sourceRow.getCell(17), targetRow.createCell(6), commonCellStyle);
        copyCellData(sourceRow.getCell(18), targetRow.createCell(10), commonCellStyle);
        targetRow.createCell(7).setCellValue("Демянчук В.М., диспетчер");
        copyCellData(sourceRow.getCell(20), targetRow.createCell(11), commonCellStyle);
        Cell employee = sourceRow.getCell(20);
        targetRow.createCell(11).setCellValue(employee.getStringCellValue() + ", инженер ООО УК СТС");
    }

    private static String[] getReportDates(Cell cell) {
        String[] dates = new String[5];
        LocalDate restorationDate = cell.getLocalDateTimeCellValue().toLocalDate();
        LocalDate failureDetectionDate = subtractRandomDays(restorationDate);

        dates[0] = failureDetectionDate.format(DATE_FORMATTER_DDMMYYYY);
        dates[1] = generateRandomTime(9, 17);
        dates[2] = failureDetectionDate.minusDays(1).format(DATE_FORMATTER_DDMMYYYY) + " "
                + generateRandomTime(0, 23);
        dates[3] = restorationDate.format(DATE_FORMATTER_DDMMYYYY);
        dates[4] = generateRandomTime(9, 17);
        return dates;
    }

    private static void copyCellData(Cell sourceCell, Cell targetCell, CellStyle style) {
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
//            }
            targetCell.setCellStyle(style);
        }
    }

    private static void concatAndCopyRowsData(Row sourceRow, Row targetRow) {
        int[] columnsToConcat = {6, 7, 9, 13}; // Колонки для объединения
        int targetColumnIndex = 5;
        StringBuilder concatenatedData = new StringBuilder();

        for (int columnIndex : columnsToConcat) {
            Cell cell = sourceRow.getCell(columnIndex);
            if (cell != null) {
                switch (cell.getCellType()) {
                    case STRING:
                        if (!concatenatedData.isEmpty()) {
                            concatenatedData.append("/").append(cell.getStringCellValue());
                        } else
                            concatenatedData.append(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            concatenatedData.append("/").append(cell.getDateCellValue().toString());
                        } else {
                            concatenatedData.append("/").append(cell.getNumericCellValue());
                        }
                        break;
                    case BOOLEAN:
                        concatenatedData.append("/").append(cell.getBooleanCellValue());
                        break;
                    default:
                        break;
                }
            } else {
                logger.warn("Ячейка пуста: строка {}, колонка {}", sourceRow.getRowNum(), columnIndex);
            }
        }

        concatenatedData.append(" (").append(getCellStringValue(sourceRow.getCell(15))).append(")");
        Cell targetCell = targetRow.createCell(targetColumnIndex);
        targetCell.setCellValue(concatenatedData.toString().trim());
    }


    public static String generateRandomTime(int mnTime, int mxTime) {
        // Задаем минимальное и максимальное время
        LocalTime minTime = LocalTime.of(mnTime, 0);
        LocalTime maxTime = LocalTime.of(mxTime, 0);

        // Преобразуем в наносекунды для диапазона
        long minNano = minTime.toNanoOfDay();
        long maxNano = maxTime.toNanoOfDay();

        // Генерируем случайное число в пределах диапазона
        long randomNano = ThreadLocalRandom.current().nextLong(minNano, maxNano);

        // Преобразуем случайное число обратно в LocalTime и возвращаем в виде строки
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//        return LocalTime.ofNanoOfDay(randomNano).truncatedTo(ChronoUnit.MINUTES);
        return LocalTime.ofNanoOfDay(randomNano).format(formatter);
    }

    private static Double getCellNumericValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    // Ignore strings that are not valid numbers
                    try {
                        return Double.parseDouble(cell.getStringCellValue());
                    } catch (NumberFormatException e) {
                        return null; // Invalid number format
                    }
                default:
                    return null;
            }
        }
        return null;
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

    private static CellStyle createDateCellStyle(Workbook resultWorkbook) {
        CellStyle dateCellStyle = resultWorkbook.createCellStyle();
        DataFormat dateFormat = resultWorkbook.createDataFormat(); // Формат даты
        dateCellStyle.setDataFormat(dateFormat.getFormat("dd.MM.YYYY"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        return dateCellStyle;
    }

    private static CellStyle createCommonCellStyle(Sheet resultSheet) {
        CellStyle simpleCellStyle = resultSheet.getWorkbook().createCellStyle();
        Row sampleRow = resultSheet.getRow(1);
        if (sampleRow != null && sampleRow.getCell(0) != null) {
            simpleCellStyle.cloneStyleFrom(sampleRow.getCell(0).getCellStyle());
        }
        return simpleCellStyle;
    }

    public static LocalDate subtractRandomDays(LocalDate date) {
        // Генерируем случайное число: 2 или 3
//        int daysToSubtract = ThreadLocalRandom.current().nextInt(2, 4); // 4 не включается, т.е. диапазон [2, 3]
        int daysToSubtract = new Random().nextInt(2, 3);
        // Отнимаем случайное количество дней
        return date.minusDays(daysToSubtract);
    }
}



