package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Добавление данных профилей в свод ИИК
public class DataFiller {
    private static final String ORDER_MONTH = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru-RU")));
    private static final String ORDER_YEAR = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("yyyy", Locale.forLanguageTag("ru-RU")));
    private static final String FOLDER_PATH = new StringBuilder().append("d:\\YandexDisk\\Отчеты ПТО АСКУЭ\\РРЭ\\")
            .append(ORDER_YEAR)
            .append("\\")
            .append(ORDER_MONTH.toUpperCase())
            .append("\\")
            .append("Профили")
            .toString();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static int count = 0;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String[] fileNames = new File(FOLDER_PATH).list((dir, name) -> name.endsWith(".xlsx"));
//        String summaryFilePath = "c:\\Users\\admin\\YandexDiskUKSTS\\YandexDisk\\ПТО РРЭ РЖД\\План ПТО 2024\\СВОД_ИИК ПТО РРЭ 2024_" + ORDER_MONTH.toUpperCase() + ".xlsx";
        String summaryFilePath = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ПТО\\СВОД_ИИК ПТО РРЭ " + ORDER_YEAR + "_" + ORDER_MONTH.toUpperCase() + ".xlsx";
//        String profileFilesPath = "c:\\Users\\admin\\YandexDiskUKSTS\\YandexDisk\\Отчеты ПТО АСКУЭ\\РРЭ\\" + ORDER_YEAR + "\\" + ORDER_MONTH.toUpperCase() + "\\Профили\\";
        String profileFilesPath = "d:\\YandexDisk\\Отчеты ПТО АСКУЭ\\РРЭ\\" + ORDER_YEAR + "\\" + ORDER_MONTH.toUpperCase() + "\\Профили\\";

        SimpleDateFormat today = new SimpleDateFormat();
        // Extract month from the profile filename
        String summaryFileName = new File(summaryFilePath).getName();
        String monthFromFileName = extractMonthFromFileName(summaryFilePath.toLowerCase());

        Map<String, Double> profileData = new HashMap<>();
        Map<String, List<String>> countersByDate = new HashMap<>();
        Map<String, String> dateByCounters = new HashMap<>();

        try {
            // Load summary workbook and sheet
            Workbook summaryWorkbook = new XSSFWorkbook(new FileInputStream(new File(summaryFilePath)));
            Sheet summarySheet = summaryWorkbook.getSheetAt(0);

            int monthColumnIndex = findMonthColumnIndex(summarySheet, monthFromFileName);
            if (monthColumnIndex == -1) {
                System.out.println("Month column not found.");
                return;
            }

            // Fill AD column in the summary file based on date match
            for (Row summaryRow : summarySheet) {
                // Extract date from the month column
                Cell monthCell = summaryRow.getCell(monthColumnIndex); // Get cell in the month column
                Cell counterCell = summaryRow.getCell(10);
                if (monthCell != null) {
                    String monthValue = monthCell.getStringCellValue(); // Assuming "Фамилия_дата"
                    String[] parts = monthValue.split("_");
                    if (parts.length > 1) {
                        String dateString = parts[1]; // Get date part
                        String counterNumber = getCellStringValue(counterCell);
                        try {
                            SIMPLE_DATE_FORMAT.parse(dateString); // Validate date format
                            countersByDate.putIfAbsent(dateString, new ArrayList<>()); // Если даты нет в мапе, создаем новый список
                            countersByDate.get(dateString).add(counterNumber); // Добавляем номер счетчика в список для этой даты
                            dateByCounters.put(counterNumber, dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            for (String date : countersByDate.keySet()) {
                String profileFilePath = profileFilesPath + "/" + "Профили нагрузки c " + date + " по " + date + ".xlsx"; // Формируем путь к файлу профиля
                File profileFile = new File(profileFilePath);

                if (profileFile.exists()) {
                    loadProfileData(profileFile, profileData, date, countersByDate); // Загружаем данные профиля
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try (Workbook summaryWorkbook = new XSSFWorkbook(new FileInputStream(new File(summaryFilePath)))) {
            Sheet summarySheet = summaryWorkbook.getSheetAt(0);

            for (Row summaryRow : summarySheet) {
                Cell counterCell = summaryRow.getCell(10); // Номер счетчика
                String counterNumber = getCellStringValue(counterCell); // Получаем значение счетчика

                // Проверяем, если есть данные для этого счетчика
                if (counterNumber != null) {
                    String key = counterNumber.trim() + "_" + dateByCounters.get(counterNumber.trim()); // Используем номер счетчика как ключ
                    if (profileData.containsKey(key)) {
                        Cell adCell = summaryRow.createCell(27); // Создаем или получаем AD ячейку
                        adCell.setCellValue(profileData.get(key)); // Заполняем ячейку числовым значением
                    }
                }
            }


            // Write changes to the summary file
            try (FileOutputStream fileOut = new FileOutputStream(summaryFilePath)) {
                summaryWorkbook.write(fileOut);
            }
            summaryWorkbook.close();

            System.out.println("Data filled successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Время выполнения: " + duration + " миллисекунд");
    }

    // Extract month from filename (example: "Профили нагрузки c 07.10.2024 по 07.10.2024 (1).xlsx")
    private static String extractMonthFromFileName(String filename) {
        // Simple logic to extract month (assuming format is consistent)
        if (filename.contains("январь")) return "январь";
        if (filename.contains("февраль")) return "февраль";
        if (filename.contains("март")) return "март";
        if (filename.contains("апрель")) return "апрель";
        if (filename.contains("май")) return "май";
        if (filename.contains("июнь")) return "июнь";
        if (filename.contains("июль")) return "июль";
        if (filename.contains("август")) return "август";
        if (filename.contains("сентябрь")) return "сентябрь";
        if (filename.contains("октябрь")) return "октябрь";
        if (filename.contains("ноябрь")) return "ноябрь";
        if (filename.contains("декабрь")) return "декабрь";
        return "";
    }

    static int findMonthColumnIndex(Sheet sheet, String month) {
        Row headerRow = sheet.getRow(0); // Assuming the first row is the header
        if (headerRow != null) {
            for (int colIndex = 0; colIndex < headerRow.getLastCellNum(); colIndex++) {
                Cell cell = headerRow.getCell(colIndex);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String cellValue = cell.getStringCellValue().toLowerCase();
                    if (cellValue.contains(month)) {
                        return colIndex; // Return the index of the month column
                    }
                }
            }
        }
        return -1; // Return -1 if not found
    }

    // Extract date from filename in the format DD.MM.YYYY
    private static String extractDateFromFileName(String filename) {
        String datePattern = "(\\d{2}\\.\\d{2}\\.\\d{4})"; // Regex to find date in the filename
        return filename.replaceAll(".*(" + datePattern + ").*", "$1"); // Extract date
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

    private static void loadProfileData(File profileFile, Map<String, Double> profileData, String date, Map<String, List<String>> counters) {
        try (Workbook profileWorkbook = new XSSFWorkbook(new FileInputStream(profileFile))) {
            Sheet profileSheet = profileWorkbook.getSheetAt(0);

            for (Row profileRow : profileSheet) {
                Cell counterCell = profileRow.getCell(2); // Столбец с номером счетчика (C)
                Cell valueCell = profileRow.getCell(16); // Столбец с показаниями (Q)

                String counterNumber = getCellStringValue(counterCell); // Получаем номер счетчика
                Double value = getCellNumericValue(valueCell); // Получаем значение

                if (counterNumber != null && value != null && counters.get(date).contains(counterNumber)) {
                    // Формируем ключ: номер счетчика_дата
                    String key = counterNumber.trim() + "_" + date;
                    profileData.put(key, value); // Сохраняем значение
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

