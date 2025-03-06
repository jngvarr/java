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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class OFOGTemplateFiller {
    private static final LocalDate today = LocalDate.now();
    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_DDMMMYYYY = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final Logger logger = LoggerFactory.getLogger(OFOGTemplateFiller.class);
    private static int iReportRows;

    public static void main(String[] args) throws IOException {
        // Путь к вашему шаблону
        String templatePath = "d:\\Downloads\\пто\\month_reports\\templates\\month_report_template.xlsx";
        String dataPath = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\ОЖ.xlsx";
        // Путь для сохранения нового файла
        String outputFilePathOFlog = "d:\\Downloads\\пто\\month_reports\\filled_operational_failure_log.xlsx";
        String outputFilePathOfogIr = "d:\\Downloads\\filled_inspection_report.xlsx";
        String pdfFilePathOFlog = "d:\\Downloads\\пто\\month_reports\\templates\\ОФОЖ.pdf";       // Результирующий файл PDF
        String pdfFilePathIReport = "d:\\Downloads\\пто\\month_reports\\templates\\Акт осмотра.pdf";       // Результирующий файл PDF
        int dateColumnIndex = 16;
        int faultReasonColNum = 18;
        int reportRows = 0;
        int ofLogSheetInsertPosition = 8;
        int iReportSheetInsertPosition = 10;

        // Открываем файл-шаблон
        try (FileInputStream templateFis = new FileInputStream(templatePath);
             XSSFWorkbook templateWorkbook = new XSSFWorkbook(templateFis)) {
            try (FileInputStream dataFis = new FileInputStream(dataPath);
                 XSSFWorkbook dataWorkbook = new XSSFWorkbook(dataFis)) {

                // Получаем первый лист
                XSSFSheet ofLogSheet = templateWorkbook.getSheet("ОФОЖ");
                XSSFSheet iReportSheet = templateWorkbook.getSheet("Акт осмотра ИИК-ИВКЭ");
                XSSFSheet dataSheet = dataWorkbook.getSheet("ОЖ");
                CellStyle commonCellStyle = createCommonCellStyle(ofLogSheet);

                setIReportDate(iReportSheet);

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
//                        int year = LocalDate.now().getYear();
//                        int month = LocalDate.now().getMonthValue();

                        //Задаем месяц отчета в ручную
                        int year = 2025;
                        int month = 2;

                        int eventYear = localDate.getYear();
                        int eventMonth = localDate.getMonthValue();
                        if (month == eventMonth && year == eventYear) {
                            logger.info("Содержание строки №{}, {}", row.getRowNum(), faultReasonCell.getStringCellValue());
                            reportRows++;
                            // Копируем строку в целевой лист
                            if (faultReasonCell.getCellType() == CellType.STRING && !faultReasonCell.getStringCellValue().trim().isEmpty()) {

                                String faultReason = faultReasonCell.getStringCellValue().trim()
                                        .replace("\u00A0", "")
                                        .replaceAll("\\s+", " "); // Убираем неразрывные пробелы и лишние пробелы
                                if (!faultReason.contains("Уточнение реквизитов ТУ (подана заявка на корректировку НСИ)")) {
                                    copyRowsData(row, ofLogSheet, ofLogSheetInsertPosition++, commonCellStyle, true);
                                } else {
                                    copyRowsData(row, iReportSheet, iReportSheetInsertPosition++, commonCellStyle, false);
                                }
                            }
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


//                PdfWriter pdfWriter1 = new PdfWriter(pdfFilePathOFlog);
//                PdfWriter pdfWriter2 = new PdfWriter(pdfFilePathIReport);
//
//                Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(pdfWriter1));

//TODO: доделать преобразование в pdf
                // Создаем таблицу PDF
// Берем первый лист из Excel
                int numberOfColumns = ofLogSheet.getRow(0).getLastCellNum();

//                Table table = new Table(numberOfColumns);
//                XSSFTable table = new XSSFTable (numberOfColumns);
//
//                // Добавляем таблицу в PDF документ
//                document.add(table);

                System.out.println("Excel файл успешно преобразован в PDF: " + pdfFilePathOFlog);


                templateWorkbook.close(); // Закрываем Workbook

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

    private static void setIReportDate(Sheet sheet) {
        String iReportNumber = "№ " + today.getMonthValue() + " от " + today.with(TemporalAdjusters.lastDayOfMonth())
                .format(DATE_FORMATTER_DDMMMYYYY);
        System.out.println(iReportNumber);
        sheet.getRow(6).getCell(0).setCellValue(iReportNumber);
    }

    private static void copyRowsData(Row sourceRow, XSSFSheet sheet, int insertPosition, CellStyle style, boolean ofog) {
        String[] dates;
        if (ofog) {
            dates = prepareOfogData(sourceRow);
        } else dates = prepareIRepData(sourceRow);

        if (insertPosition <= sheet.getLastRowNum()) {
            sheet.shiftRows(insertPosition, sheet.getLastRowNum(), 1);
        }

        Row targetRow = sheet.createRow(insertPosition);
        writeRowData(targetRow, dates, style);
    }

    private static String[] prepareIRepData(Row sourceRow) {

        String equipment;
        String equipmentNum;
        String equipmentType;
        String row = getCellStringValue(sourceRow.getCell(13));

        if (row.isEmpty()) {
            equipment = "ИВЭК";
            equipmentNum = getCellStringValue(sourceRow.getCell(14));
            equipmentType = "DC1000";
        } else {
            equipmentNum = getCellStringValue(sourceRow.getCell(13));
            equipmentType = getCellStringValue(sourceRow.getCell(12));
            equipment = "ИИК";
        }
        return new String[]{
                String.valueOf(++iReportRows),
                equipment,
                equipmentType,
                equipmentNum,
                getCellStringValue(sourceRow.getCell(7)),
                getCellStringValue(sourceRow.getCell(9)),
                getCellStringValue(sourceRow.getCell(4)),
                getCellStringValue(sourceRow.getCell(5)),
                getCellStringValue(sourceRow.getCell(21)),
        };
    }

    private static String[] prepareOfogData(Row sourceRow) {
        Cell cell = sourceRow.getCell(16);
        LocalDate restorationDate = cell.getLocalDateTimeCellValue().toLocalDate();
        LocalDate failureDetectionDate = subtractRandomDays(restorationDate);

        return new String[]{
                failureDetectionDate.format(DATE_FORMATTER_DDMMYYYY),
                generateRandomTime(9, 17),
                failureDetectionDate.minusDays(1).format(DATE_FORMATTER_DDMMYYYY) + " " + generateRandomTime(0, 23),
                "ЗСЖД",
                getCellStringValue(sourceRow.getCell(7)),
                concatAndCopyRowsData(sourceRow),
                getCellStringValue(sourceRow.getCell(17)),
                "диспетчер ООО \"УК СТС\"",
                restorationDate.format(DATE_FORMATTER_DDMMYYYY),
                generateRandomTime(9, 17),
                getCellStringValue(sourceRow.getCell(18)),
                getCellStringValue(sourceRow.getCell(20)) + ", инженер ООО \"УК СТС\""
        };
    }

    private static void writeRowData(Row targetRow, String[] data, CellStyle style) {
        for (int i = 0; i < data.length; i++) {
            Cell targetCell = targetRow.createCell(i);
            targetCell.setCellValue(data[i]);
            targetCell.setCellStyle(style);
            targetCell.getCellStyle().setWrapText(true);
        }
    }

    private static String concatAndCopyRowsData(Row sourceRow) {
        int[] columnsToConcat = {6, 7, 9}; // Колонки для объединения
        StringBuilder concatenatedData = new StringBuilder();

        for (int columnIndex : columnsToConcat) {
            Cell cell = sourceRow.getCell(columnIndex);
            if (cell != null) {
                String cellValue = getCellStringValue(cell);
                if (cellValue != null && !cellValue.isEmpty()) {
                    if (concatenatedData.length() > 0) {
                        concatenatedData.append("/");
                    }
                    concatenatedData.append(cellValue);
                }
            }
        }
        concatenatedData.append(" (").append(getCellStringValue(sourceRow.getCell(13))).append(")");
        return concatenatedData.toString().trim();
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
//        return LocalTime.ofNanoOfDay(randomNano).truncatedTo(ChronoUnit.MINUTES);
        return LocalTime.ofNanoOfDay(randomNano).format(TIME_FORMATTER);
    }


    private static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    return dateFormat.format(cell.getDateCellValue());
                } else {
                    return new DecimalFormat("0").format(cell.getNumericCellValue());
                }
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e) {
                    return cell.getStringCellValue();
                }
            default:
                return "";
        }
    }


    private static CellStyle createDateCellStyle(Workbook resultWorkbook) {
        CellStyle dateCellStyle = resultWorkbook.createCellStyle();
        DataFormat dateFormat = resultWorkbook.createDataFormat(); // Формат даты
        dateCellStyle.setDataFormat(dateFormat.getFormat("dd.MM.YYYY"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        return dateCellStyle;
    }

    static CellStyle createCommonCellStyle(Sheet resultSheet) {
        CellStyle simpleCellStyle = resultSheet.getWorkbook().createCellStyle();
        simpleCellStyle.setBorderBottom(BorderStyle.THIN);
        simpleCellStyle.setBorderLeft(BorderStyle.THIN);
        simpleCellStyle.setBorderRight(BorderStyle.THIN);
        simpleCellStyle.setBorderTop(BorderStyle.THIN);
        return simpleCellStyle;
    }

    public static LocalDate subtractRandomDays(LocalDate date) {
        // Генерируем случайное число: 2 или 3
//        int daysToSubtract = ThreadLocalRandom.current().nextInt(2, 4); // 4 не включается, т.е. диапазон [2, 3]
        int daysToSubtract = new Random().nextInt(2, 4);
        // Отнимаем случайное количество дней
        return date.minusDays(daysToSubtract);
    }
}



