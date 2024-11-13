package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;

public class DaysDataFiller {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        LocalDate localDateToday = LocalDate.now();
        String today = localDateToday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

//        String planOTOPath = "c:\\Users\\admin\\YandexDiskUKSTS\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\Контроль ПУ РРЭ (Задания на ОТО РРЭ)" + ".xlsx"; //дома
        String planOTOPath = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx";
        String planOTOFile = new File(planOTOPath).getName();

//        String folderPath = "d:\\Downloads\\профили2\\reports\\" + today;                              //дома
        String folderPath = "d:\\загрузки\\PTO\\reports\\" + today;
        String[] fileNames = new File(folderPath).list((dir, name) -> name.endsWith(".xlsx"));

        Map<String, String> normallyTurnedOff = new HashMap<>();
        Map<String, String> iikStatus = new HashMap<>();
        Map<String, String> connectionDiag = new HashMap<>();
        Map<String, String> dataControl = new HashMap<>();
        int enabled = 0;

        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(planOTOPath))) {
            Sheet iikSheet = planOTOWorkbook.getSheet("ИИК");
            Sheet ivkeSheet = planOTOWorkbook.getSheet("ИВКЭ");

            Row firstRow = iikSheet.getRow(0);
            int lastColumnNum = firstRow.getLastCellNum();


            for (String file : fileNames) {
                if (file.startsWith("Контроль поступления данных")) {
                    dataControl = fillingMapWithData(1, 5, folderPath + "/" + file);
                }
                if (file.startsWith("Состав ИИК")) {
                    normallyTurnedOff = fillingMapWithData(11, 9, folderPath + "/" + file);
                }
//                if (file.startsWith("report1")) { //Статусы ПУ (Echelon)
                if (file.startsWith("Статусы ПУ")) { //Статусы ПУ (Echelon)
                    iikStatus = fillingMapWithData(11, 12, folderPath + "/" + file);
                }
//                if (file.startsWith("report2")) { //Диагностика связи с УСПД/ПУ
                if (file.startsWith("Диагностика связи")) { //Диагностика связи с УСПД/ПУ
                    connectionDiag = fillingMapWithData(9, 11, folderPath + "/" + file);
                }
            }

            for (Row iikRow : iikSheet) {
                Cell counterCell = iikRow.getCell(13); // Номер счетчика
                String counterNumber = getCellStringValue(counterCell); // Получаем значение счетчика

                if (counterNumber != null) {
                    String key = counterNumber.trim();
                    if (dataControl.containsKey(key)) {
                        Cell dataControlCol = iikRow.createCell(lastColumnNum);
                        String profile = dataControl.get(key);
                        if (profile.equals("Достоверные")) enabled++;
                        dataControlCol.setCellValue(profile);
                    }

                    if (normallyTurnedOff.containsKey(key)) {
                        Cell dataTurnedOffCol = iikRow.createCell(17);
                        dataTurnedOffCol.setCellValue(normallyTurnedOff.get(key));
                    }

                    if (iikStatus.containsKey(key)) {
                        Cell iikStatusCol = iikRow.createCell(19);
                        iikStatusCol.setCellValue(iikStatus.get(key));
                    }
                }
            }

            DataFormat poiDataFormat = ivkeSheet.getWorkbook().createDataFormat();
            CellStyle dateCellStyle = ivkeSheet.getWorkbook().createCellStyle();
            dateCellStyle.setDataFormat(poiDataFormat.getFormat("dd.MM.yyyy"));
            for (Row ivkeRow : ivkeSheet) {
                Cell ivkeCell = ivkeRow.getCell(9);
                String ivkeNumber = getCellStringValue(ivkeCell);
                if (ivkeNumber != null) {
                    String key = ivkeNumber.trim();
                    if (connectionDiag.containsKey(key)) {
                        Cell connectionDiagCol = ivkeRow.createCell(11);
                        connectionDiagCol.setCellValue(connectionDiag.get(key));
                        String dateString = connectionDiag.get(key);

                        // Попытка преобразовать строку в дату
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                            connectionDiagCol.setCellValue(sdf.parse(dateString));
                            connectionDiagCol.setCellStyle(dateCellStyle); // Устанавливаем стиль даты
                        } catch (ParseException e) {
                            // Если дата не распознана, сохраняем как текст
                            connectionDiagCol.setCellValue(dateString);
                        }
                    }
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(planOTOPath)) {
                Cell dataControlCell = firstRow.createCell(lastColumnNum);
                dataControlCell.setCellValue("Профили на " + today + " на интервале последних 7 дней" + " (" + enabled + ")");
//            dataControlCell.setCellValue("Профили на " + localDateToday.format(DateTimeFormatter.ofPattern("dd MMMM"))
//                    + " на интервале последних 7 дней" + " (" + enabled + ")");

                planOTOWorkbook.write(fileOut);
            }
            System.out.println("Data filled successfully!");


        } catch (
                IOException ex) {
            throw new RuntimeException(ex);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Время выполнения: " + duration / 1000 + " секунд(ы)");
    }


    // Extract date from filename in the format DD.MM.YYYY
    private static Map<String, String> fillingMapWithData(int column, int neededDataColumn, String fileName) {
        Map<String, String> workMap = new HashMap<>();
        try (Workbook currentWorkbook = new XSSFWorkbook(new FileInputStream(fileName))) {
            Sheet workbookSheet = currentWorkbook.getSheetAt(0);
            for (Row row : workbookSheet) {
                Cell cellKey = row.getCell(column);
                Cell cellValue = row.getCell(neededDataColumn);
                if (cellKey != null && cellValue != null) {
                    String key = getCellStringValue(cellKey);
                    String value = getCellStringValue(cellValue);
                    if (key != null && value != null) {
                        workMap.put(key, value);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return workMap;
    }

    // Get string value from cell safely
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

    // Get numeric value from cell safely
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
}