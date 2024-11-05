import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelFormatter {

    public static void main(String[] args) {
        String folderPath = "d:\\загрузки\\профили2\\";

        String[] fileNames = new File(folderPath).list((dir, name) -> name.endsWith(".xlsx"));
        if (fileNames == null) return;

        for (String fileName : fileNames) {
            File file = new File(folderPath + fileName);
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);

                // Преобразование столбца C в числовой формат
                for (Row row : sheet) {
                    Cell cell = row.getCell(2); // Индекс 2 соответствует столбцу C
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        try {
                            double numericValue = Double.parseDouble(cell.getStringCellValue());
                            cell.setCellValue(numericValue);
                        } catch (NumberFormatException e) {
                            System.out.println("Не удалось преобразовать значение в строке " + row.getRowNum());
                        }
                    }
                }

                // Извлечение даты из имени файла
                String dateStr = extractDateFromFilename(fileName);
                if (dateStr != null) {
                    // Преобразование строки даты в нужный формат
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
                    Date date = inputFormat.parse(dateStr);

                    SimpleDateFormat outputFormat = new SimpleDateFormat("d");
                    String newFilename = outputFormat.format(date) + ".xlsx";

                    // Сохранение измененного файла
                    try (FileOutputStream fos = new FileOutputStream(new File(folderPath + newFilename))) {
                        workbook.write(fos);
                        System.out.println("Файл сохранён как " + newFilename);
                    }
                } else {
                    System.out.println("Не удалось извлечь дату из имени файла.");
                }

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для извлечения даты из названия файла
    private static String extractDateFromFilename(String filename) {
        Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");
        Matcher matcher = pattern.matcher(filename);
        return matcher.find() ? matcher.group(0) : null;
    }
}

