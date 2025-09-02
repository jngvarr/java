package jngvarr.ru.pto_ackye_rzhd.services;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.FileManagement.*;
import static jngvarr.ru.pto_ackye_rzhd.telegram.FileManagement.OPERATION_LOG_PATH;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.eelToNtel;

@Data
@Slf4j
@Component
//@RequiredArgsConstructor
public class ExcelFileService {

    public void copyRow(Row sourceRow, Row targetRow, int columnCount) {
        for (int i = 0; i <= columnCount; i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);
            Workbook operationLog = targetRow.getSheet().getWorkbook();

            CellStyle defaultCellStyle = createCommonCellStyle(operationLog);
            CellStyle dateCellStyle = createDateCellStyle(operationLog, "dd.MM.YYYY", "Calibri");

            if (sourceCell != null) {
                switch (sourceCell.getCellType()) {
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(sourceCell)) {
                            targetCell.setCellValue(sourceCell.getDateCellValue()); // Копируем дату
                            targetCell.setCellStyle(dateCellStyle); // Применяем стиль для даты
                        } else {
                            targetCell.setCellValue(sourceCell.getNumericCellValue()); // Копируем число
                            targetCell.setCellStyle(defaultCellStyle);
                        }
                        break;
                    case BOOLEAN:
                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                    case FORMULA:
                        targetCell.setCellFormula(sourceCell.getCellFormula());
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                    default:
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                }
            }
        }
    }
    CellStyle createDateCellStyle(Workbook resultWorkbook, String format, String font) {
        CellStyle dateCellStyle = resultWorkbook.createCellStyle();
        DataFormat dateFormat = resultWorkbook.createDataFormat(); // Формат даты
        dateCellStyle.setDataFormat(dateFormat.getFormat(format));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setFont(createCellFontStyle(resultWorkbook, font, (short) 10, false));
        return dateCellStyle;
    }


    CellStyle createCommonCellStyle(Workbook resultWorkbook) {
        CellStyle simpleCellStyle = resultWorkbook.createCellStyle();
        Font font = createCellFontStyle(resultWorkbook, "Arial", (short) 10, false);

        simpleCellStyle.setBorderBottom(BorderStyle.THIN);
        simpleCellStyle.setBorderLeft(BorderStyle.THIN);
        simpleCellStyle.setBorderRight(BorderStyle.THIN);
        simpleCellStyle.setBorderTop(BorderStyle.THIN);
        simpleCellStyle.setFont(font);
        return simpleCellStyle;
    }

    private Font createCellFontStyle(Workbook workbook, String fontName, short fontSize, boolean isBold) {
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        font.setBold(isBold);
        return font;
    }

    /**
     * Метод преобразования значения ячейки в дату, так чтоб Excel понимал его именно как дату.
     *
     * @param date Cell, ячейка содержащая значеие даты.
     */
    public void setDateCellStyle(Cell date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        CellStyle dateStyle = createDateCellStyle(date.getRow().getSheet().getWorkbook(), "dd.MM.yy", "Arial");
        try {
            date.setCellValue(sdf.parse(straightFormattedCurrentDate));
        } catch (ParseException e) {
            date.setCellStyle(dateStyle);
        }
        date.setCellStyle(dateStyle);
    }

    public void clearCellData(int[] ints, Row row) {
        for (int anInt : ints) {
            row.createCell(anInt).setCellValue("");
        }
    }
    public String getCellStringValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return new SimpleDateFormat("dd.MM.yyyy").format(cell.getDateCellValue());
                    } else {
                        return new DecimalFormat("0").format(cell.getNumericCellValue());
                    }
                case FORMULA:
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    return evaluator.evaluate(cell).getStringValue();
                default:
                    return null;
            }
        }
        return null;
    }

    public int findColumnIndex(Sheet sheet, String columnName) {
        Row headerRow = sheet.getRow(0); // Заголовок на первой строке
        if (headerRow == null) return -1;

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getStringCellValue().toLowerCase().startsWith(columnName.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    public Map<String, String> getPhotoSavingPathFromExcel() {

        ExcelFileService excelFileService = new ExcelFileService();

        Map<String, String> paths = null;
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH))) {
            paths = new HashMap<>();
            Sheet iikSheet = planOTOWorkbook.getSheet("ИИК");
            int meterNumberColumnIndex = excelFileService.findColumnIndex(iikSheet, "Номер счетчика");
            int dcNumberColumnIndex = excelFileService.findColumnIndex(iikSheet, "Номер УСПД");
            int eelColumnIndex = excelFileService.findColumnIndex(iikSheet, "ЭЭЛ");
            int stationColumnIndex = excelFileService.findColumnIndex(iikSheet, "Железнодорожная станция");
            int substationColumnIndex = excelFileService.findColumnIndex(iikSheet, "ТП/КТП");
            int meterPointIndex = excelFileService.findColumnIndex(iikSheet, "Точка учёта");
            for (Row row : iikSheet) {
                String meterNum = excelFileService.getCellStringValue(row.getCell(meterNumberColumnIndex));
                String dcNum = excelFileService.getCellStringValue(row.getCell(dcNumberColumnIndex));
                if (meterNum != null) {
                    paths.put(meterNum,
                            eelToNtel.get(row.getCell(eelColumnIndex).getStringCellValue()) + "\\" +
                                    row.getCell(stationColumnIndex).getStringCellValue() + "\\" +
                                    row.getCell(substationColumnIndex).getStringCellValue() + "\\" +
                                    row.getCell(meterPointIndex).getStringCellValue());
                }
                if (dcNum != null) {
                    paths.putIfAbsent(dcNum,
                            eelToNtel.get(row.getCell(eelColumnIndex).getStringCellValue()) + "\\" +
                                    row.getCell(stationColumnIndex).getStringCellValue() + "\\" +
                                    row.getCell(substationColumnIndex).getStringCellValue() + "\\");
                }
            }
        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
        return paths;
    }

    // Метод для проверки и преобразования номера счетчика
    public Object parseMeterNumber(String meterNumberStr) {
        try {
            return Long.parseLong(meterNumberStr);
        } catch (NumberFormatException e) {
            return meterNumberStr;
        }
    }

    private void sheetsFilling(long userId) {
        if (otoLog.isEmpty()) return;
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             Workbook operationLog = new XSSFWorkbook(new FileInputStream(OPERATION_LOG_PATH));
             FileOutputStream fileOut = new FileOutputStream(OPERATION_LOG_PATH);
             FileOutputStream fileOtoOut = new FileOutputStream(PLAN_OTO_PATH);
        ) {

            boolean isDcWorks = containsDcWorks();
            boolean isDcChange = containsDcChange();

            Sheet meterWorkSheet = planOTOWorkbook.getSheet("ИИК");
            Sheet operationLogSheet = operationLog.getSheet("ОЖ");

            String taskOrder = dataPreparing(operationLogSheet, meterWorkSheet, isDcWorks);

            if (isDcWorks) {
                fillDcSection(planOTOWorkbook.getSheet("ИВКЭ"), taskOrder, isDcChange);
            }
            operationLog.write(fileOut);
            planOTOWorkbook.write(fileOtoOut);
            otoLog.clear();

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
    }

    public void copyAndFillLogRow(Row otoRow, Row newLogRow, int orderColumnNumber, String taskOrder, List<String> columns) {
        copyRow(otoRow, newLogRow, orderColumnNumber);
        Cell date = newLogRow.getCell(16);
        setDateCellStyle(date);
        newLogRow.getCell(17).setCellValue(columns.get(0));
        newLogRow.getCell(18).setCellValue(columns.get(1));
        newLogRow.createCell(19);
//        newLogRow.getCell(19).setCellValue("");
        newLogRow.getCell(20).setCellValue("Исполнитель"); //TODO: взять исполнителя из БД по userId
        newLogRow.getCell(21).setCellValue(taskOrder);
        otoRow.getCell(orderColumnNumber).setCellValue(taskOrder);
//      newLogRow.createCell(22).setCellValue("Выполнено");   //TODO: добавить после реализации внесения корректировок в Горизонт либо БД
    }
}
