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

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.PHOTO_SUBDIRS_NAME;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.FORMATTED_CURRENT_DATE;
import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.TODAY;

@Data
@Component
@RequiredArgsConstructor
public class StringUtils {
    private final ExcelFileService excelFileService;
    private final ExcelUtil excelUtil;
    private static final String WORKING_FOLDER = "\\" + TODAY.getYear() + "\\" + TODAY.format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru-RU"))).toUpperCase();
    public static final String PHOTO_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\ФОТО (Подтверждение работ)\\" + WORKING_FOLDER;
    private final Map<String, String> savingPaths = new HashMap<>();


    @PostConstruct
    private void init() {
        savingPaths.putAll(excelFileService.getPhotoSavingPathFromExcel());
    }

    public String createSavingPath(PendingPhoto pending, long userId, TBotConversationStateService conversationStateService) {
        String tempPath = "";
        OtoType operationType = conversationStateService.getOtoType(userId);
        String baseDir = PHOTO_PATH + File.separator;
        String path = savingPaths.getOrDefault(pending.getDeviceNumber(), "unknown");

        if (operationType != null) {
            if (PHOTO_SUBDIRS_NAME.containsKey(operationType)) {
                baseDir += PHOTO_SUBDIRS_NAME.get(operationType) + File.separator;
            }
        } else if (!conversationStateService.getProcessState(userId).equals(ProcessState.WAITING_FOR_DC_PHOTO))
            path = path.substring(0, path.lastIndexOf("\\"));

        if (conversationStateService.getPhotoState(userId).getUploadedPhotos().isEmpty()) tempPath = path;
        else if (conversationStateService.getPhotoState(userId).getUploadedPhotos().size() < 2) path = tempPath;
        return baseDir + path;
    }

    public String createNewFileName(PendingPhoto pending, Long userId, TBotConversationStateService conversationStateService) {
        OtoType operationType = conversationStateService.getOtoType(userId);
        String photoSuffix = "";
        String additionalInfo = pending.getAdditionalInfo() != null ? "_" + pending.getAdditionalInfo() : "";
        if (operationType != null) {
            PhotoState photoState = conversationStateService.getPhotoState(userId);
            photoSuffix = getSavingPhotoSuffix(operationType, photoState);
            if (operationType == OtoType.TT_CHANGE) {
                additionalInfo = "_(" + photoState.getNextPhotoType(operationType) + additionalInfo.replace("_", ", №") + ")";
            } else {
                additionalInfo = "";
            }
        }
        return FORMATTED_CURRENT_DATE + "_" + getSavingPhotoPrefix(pending.getType()) + pending.getDeviceNumber() +
                additionalInfo + photoSuffix + ".jpg";
    }

    private String getSavingPhotoPrefix(String type) {
        return switch (type) {
            case "counter" -> "ИИК_";
            case "concentrator" -> "ИВКЭ_";
            case "tt" -> "ТТ_";
            default -> "unknown_";
        };
    }

    private String getSavingPhotoSuffix(OtoType operationType, PhotoState state) {
        if (operationType != null && (operationType.equals(OtoType.METER_CHANGE)
                || operationType.equals(OtoType.DC_CHANGE))) {
            return "_" + state.getNextPhotoType(operationType);
        } else return "";
    }

//    public String getCellStringValue(Cell cell) {
//        if (cell != null) {
//            switch (cell.getCellType()) {
//                case STRING:
//                    return cell.getStringCellValue();
//                case NUMERIC:
//                    if (DateUtil.isCellDateFormatted(cell)) {
//                        return new SimpleDateFormat("dd.MM.yyyy").format(cell.getDateCellValue());
//                    } else {
//                        return new DecimalFormat("0").format(cell.getNumericCellValue());
//                    }
//                case FORMULA:
//                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
//                    return evaluator.evaluate(cell).getStringValue();
//                default:
//                    return null;
//            }
//        }
//        return null;
//    }


    public String buildSubstationMapKey(Substation substation) {
        return new StringBuilder().
                append(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getRegion().getName()).
                append("_").
                append(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getName()).
                append("_").
                append(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getName()).
                append("_").
                append(substation.getStation().getPowerSupplyDistrict().getName()).
                append("_").
                append(substation.getStation().getName()).
                append("_").
                append(substation.getName()).toString();

    }
    public String getStringMapKey(Row row) {
        return new StringBuilder()
                .append(excelUtil.getCellStringValue(row.getCell(2)))
                .append("_")
                .append(excelUtil.getCellStringValue(row.getCell(3)))
                .append("_")
                .append(excelUtil.getCellStringValue(row.getCell(4)))
                .append("_")
                .append(excelUtil.getCellStringValue(row.getCell(5)))
                .append("_")
                .append(excelUtil.getCellStringValue(row.getCell(6)))
                .append("_")
                .append(excelUtil.getCellStringValue(row.getCell(7)))
                .toString();
    }

}
