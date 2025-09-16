package jngvarr.ru.pto_ackye_rzhd.application.util;

import jakarta.annotation.PostConstruct;
import jngvarr.ru.pto_ackye_rzhd.application.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.Substation;
import jngvarr.ru.pto_ackye_rzhd.domain.value.OtoType;
import jngvarr.ru.pto_ackye_rzhd.domain.value.PendingPhoto;
import jngvarr.ru.pto_ackye_rzhd.domain.value.PhotoState;
import jngvarr.ru.pto_ackye_rzhd.domain.value.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.application.services.TBotConversationStateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.PHOTO_SUBDIRS_NAME;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.FORMATTED_CURRENT_DATE;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.TODAY;

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
