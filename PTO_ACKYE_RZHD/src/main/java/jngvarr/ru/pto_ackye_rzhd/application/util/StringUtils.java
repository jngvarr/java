package jngvarr.ru.pto_ackye_rzhd.application.util;

import jngvarr.ru.pto_ackye_rzhd.domain.entities.Substation;
import lombok.Data;
import org.apache.poi.ss.usermodel.Row;

import java.util.StringJoiner;

@Data
public final class StringUtils {

    public static String buildSubstationMapKey(Substation substation) {
        return new StringJoiner("_").
                add(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getRegion().getName()).
                add(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getName()).
                add(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getName()).
                add(substation.getStation().getPowerSupplyDistrict().getName()).
                add(substation.getStation().getName()).
                add(substation.getName()).toString();

    }

    public static String getStringMapKey(Row row, ExcelUtil excelUtil) {
        return new StringJoiner("_")
                .add(excelUtil.getCellStringValue(row.getCell(2)))
                .add(excelUtil.getCellStringValue(row.getCell(3)))
                .add(excelUtil.getCellStringValue(row.getCell(4)))
                .add(excelUtil.getCellStringValue(row.getCell(5)))
                .add(excelUtil.getCellStringValue(row.getCell(6)))
                .add(excelUtil.getCellStringValue(row.getCell(7)))
                .toString();
    }
}
