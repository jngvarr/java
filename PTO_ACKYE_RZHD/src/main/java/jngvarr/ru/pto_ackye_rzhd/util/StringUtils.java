package jngvarr.ru.pto_ackye_rzhd.util;

import jngvarr.ru.pto_ackye_rzhd.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.OtoType;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.PendingPhoto;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.PhotoState;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.telegram.services.TBotConversationStateService;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.PHOTO_SUBDIRS_NAME;
import static jngvarr.ru.pto_ackye_rzhd.util.DateUtils.FORMATTED_CURRENT_DATE;
import static jngvarr.ru.pto_ackye_rzhd.util.DateUtils.TODAY;

@Data
@Component
public class StringUtils {
    private final ExcelFileService excelFileService;
    private final TBotConversationStateService conversationStateService;
    private static final String WORKING_FOLDER = "\\" + TODAY.getYear() + "\\" + TODAY.format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru-RU"))).toUpperCase();
    public static final String PHOTO_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\ФОТО (Подтверждение работ)\\" + WORKING_FOLDER;
    private Map<String, String> savingPaths = excelFileService.getPhotoSavingPathFromExcel();//TODO подумать

    public String createSavingPath(PendingPhoto pending, long userId) {
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

    public String createNewFileName(PendingPhoto pending, Long userId) {
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

}
