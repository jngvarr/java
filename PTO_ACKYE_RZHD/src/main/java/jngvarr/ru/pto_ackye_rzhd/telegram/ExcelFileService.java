package jngvarr.ru.pto_ackye_rzhd.telegram;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static jngvarr.ru.pto_ackye_rzhd.telegram.FileManagement.straightFormattedCurrentDate;

@Data
@Slf4j
@Component
//@RequiredArgsConstructor
public class ExcelFileService {

    void copyRow(Row sourceRow, Row targetRow, int columnCount, CellStyle defaultCellStyle, CellStyle dateCellStyle) {
        for (int i = 0; i <= columnCount; i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);

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
    void setDateCellStyle(Cell date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        CellStyle dateStyle = createDateCellStyle(date.getRow().getSheet().getWorkbook(), "dd.MM.yy", "Arial");
        try {
            date.setCellValue(sdf.parse(straightFormattedCurrentDate));
        } catch (ParseException e) {
            date.setCellStyle(dateStyle);
        }
        date.setCellStyle(dateStyle);
    }

    String getCellStringValue(Cell cell) {
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

    int findColumnIndex(Sheet sheet, String columnName) {
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
}
