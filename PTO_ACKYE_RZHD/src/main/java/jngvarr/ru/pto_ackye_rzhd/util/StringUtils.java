package jngvarr.ru.pto_ackye_rzhd.util;

import jngvarr.ru.pto_ackye_rzhd.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.telegram.FileManagement;
import jngvarr.ru.pto_ackye_rzhd.telegram.PendingPhoto;
import jngvarr.ru.pto_ackye_rzhd.telegram.PhotoState;
import jngvarr.ru.pto_ackye_rzhd.telegram.TBot;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.FileManagement.PHOTO_PATH;
import static jngvarr.ru.pto_ackye_rzhd.telegram.FileManagement.formattedCurrentDate;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.PHOTO_SUBDIRS_NAME;

@Data
@Component
public class StringUtils {
    private final FileManagement fileManagement;
    private final ExcelFileService excelFileService;
    private Map<String, String> savingPaths = excelFileService.getPhotoSavingPathFromExcel();//TODO подумать

    public String createSavingPath(PendingPhoto pending, long userId) {
        String chgePath;
        TBot.OtoType operationType = otoTypes.get(userId);
        String baseDir = PHOTO_PATH + File.separator;
        String path = savingPaths.getOrDefault(pending.getDeviceNumber(), "unknown");

        if (operationType != null) {
            if (PHOTO_SUBDIRS_NAME.containsKey(operationType)) {
                baseDir += PHOTO_SUBDIRS_NAME.get(operationType) + File.separator;
            }
        } else if (!processStates.get(userId).equals(TBot.ProcessState.WAITING_FOR_DC_PHOTO))
            path = path.substring(0, path.lastIndexOf("\\"));

        if (photoStates.get(userId).getUploadedPhotos().isEmpty()) chgePath = path;
        else if (photoStates.get(userId).getUploadedPhotos().size() < 2) path = chgePath;
        return baseDir + path;
    }

    public String createNewFileName(PendingPhoto pending, Long userId) {
        TBot.OtoType operationType = otoTypes.get(userId);
        String photoSuffix = "";
        String additionalInfo = pending.getAdditionalInfo() != null ? "_" + pending.getAdditionalInfo() : "";
        if (operationType != null) {
            PhotoState photoState = photoStates.get(userId);
            photoSuffix = getSavingPhotoSuffix(operationType, photoState);
            if (operationType == TBot.OtoType.TT_CHANGE) {
                additionalInfo = "_(" + photoState.getNextPhotoType(operationType) + additionalInfo.replace("_", ", №") + ")";
            } else {
                additionalInfo = "";
            }
        }
        return formattedCurrentDate + "_" + getSavingPhotoPrefix(pending.getType()) + pending.getDeviceNumber() +
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

    private String getSavingPhotoSuffix(TBot.OtoType operationType, PhotoState state) {
        if (operationType != null && (operationType.equals(TBot.OtoType.METER_CHANGE)
                || operationType.equals(TBot.OtoType.DC_CHANGE))) {
            return "_" + state.getNextPhotoType(operationType);
        } else return "";
    }

}
