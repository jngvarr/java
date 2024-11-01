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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFiller {
    public static void main(String[] args) {
        String summaryFilePath = "d:\\Downloads\\профили2\\свод\\ИИК ПТО РРЭ 2024_ОКТЯБРЬ_СВОД.xlsx";
        String profileFilePath = "d:\\Downloads\\профили2\\Профили нагрузки c 07.10.2024 по 07.10.2024 (1).xlsx";

        // Extract month from the profile filename
        String profileFileName = new File(profileFilePath).getName();
        String summaryFileName = new File(summaryFilePath).getName();
        String monthFromFileName = extractMonthFromFileName(summaryFilePath.toLowerCase());
        String profileDate = extractDateFromFileName(profileFileName);

        try {
            // Load summary workbook and sheet
            Workbook summaryWorkbook = new XSSFWorkbook(new FileInputStream(new File(summaryFilePath)));
            Sheet summarySheet = summaryWorkbook.getSheetAt(0);

            // Load profile workbook and sheet
            Workbook profileWorkbook = new XSSFWorkbook(new FileInputStream(new File(profileFilePath)));
            Sheet profileSheet = profileWorkbook.getSheetAt(0);

            // Create a map to hold profile data (counter number as key and value from column Q)
            Map<String, Double> profileData = new HashMap<>();

            // Read profile data from column C and Q
            for (Row profileRow : profileSheet) {
                Cell counterCell = profileRow.getCell(2); // Column C (№ счетчика)
                Cell valueCell = profileRow.getCell(16); // Column Q (Показания на 12:00)

                String counterNumber = getCellStringValue(counterCell);
                Double value = getCellNumericValue(valueCell);

                if (counterNumber != null && value != null) {
                    profileData.put(counterNumber.trim(), value); // Store the value in the map
                }
            }

            // Fill AD column in the summary file based on date match
            for (Row summaryRow : summarySheet) {
                Cell dateCell = summaryRow.getCell(27); // Column AB (дата)
                Cell counterCell = summaryRow.getCell(10); // Column K (Номер счетчика)

                String summaryDate = getCellStringValue(dateCell); // Get the date value safely
                String counterNumber = getCellStringValue(counterCell); // Get the counter number safely

                // Check if both date and counter number are valid
                if (summaryDate != null && counterNumber != null) {
                    // Check if the date matches and counter number is in profileData
                    if (summaryDate.equals(profileDate) && profileData.containsKey(counterNumber.trim())) {
                        Double fillValue = profileData.get(counterNumber.trim());
                        Cell adCell = summaryRow.createCell(29); // Create or get existing AD cell
                        adCell.setCellValue(fillValue); // Fill the cell with numeric value
                    }
                }
            }
//            // Find the column index for the month in the summary file
//            int monthColumnIndex = findMonthColumnIndex(summarySheet, monthFromFileName);
//            if (monthColumnIndex == -1) {
//                System.out.println("Month column not found.");
//                return;
//            }
//
//            // Fill AD column in the summary file based on extracted dates
//            for (Row summaryRow : summarySheet) {
//                Cell counterCell = summaryRow.getCell(10); // Column K (Номер счетчика)
//
//                String counterNumber = getCellStringValue(counterCell); // Get the counter number safely
//
//                // Check if counter number is valid
//                if (counterNumber != null) {
//                    Double fillValue = profileData.get(counterNumber.trim());
//                    if (fillValue != null) {
//                        Cell adCell = summaryRow.createCell(29); // Create or get existing AD cell
//                        adCell.setCellValue(fillValue); // Fill the cell with numeric value
//                    }
//                }
//                // Extract date from the month column
//                Cell monthCell = summaryRow.getCell(monthColumnIndex); // Get cell in the month column
//                if (monthCell != null) {
//                    String monthValue = monthCell.getStringCellValue(); // Assuming "Фамилия_дата"
//                    String[] parts = monthValue.split("_");
//                    if (parts.length > 1) {
//                        String dateString = parts[1]; // Get date part
//                        try {
//                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//                            sdf.parse(dateString); // Validate date format
//                            // Use this date instead of AB for further processing if needed
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }

            // Write changes to the summary file
            try (FileOutputStream fileOut = new FileOutputStream(summaryFilePath)) {
                summaryWorkbook.write(fileOut);
            }
            summaryWorkbook.close();
            profileWorkbook.close();

            System.out.println("Data filled successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static int findMonthColumnIndex(Sheet sheet, String month) {
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
}