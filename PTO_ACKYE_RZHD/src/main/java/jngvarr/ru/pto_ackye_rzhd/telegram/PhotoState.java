package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.telegram.TBot.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class PhotoState {
    private final String deviceNumber;
    private final Set<String> uploadedPhotos = new HashSet<>();

    // Для ТТ определяем порядок фаз A → B → C
    private static final List<String> TT_PHASES = List.of("ф.A", "ф.B", "ф.C");

    public PhotoState(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public boolean isPhotoUploaded(String phase) {
        return uploadedPhotos.contains(phase);
    }

    public void markPhotoUploaded(String phase) {
        uploadedPhotos.add(phase);
    }

    public boolean isComplete(OtoType operationType) {
        return operationType == OtoType.TT_CHANGE
                ? uploadedPhotos.containsAll(TT_PHASES) : uploadedPhotos.containsAll(Set.of("демонтирован", "установлен"));
    }

    public String getNextPhotoType(OtoType operationType) {
        if (operationType == OtoType.TT_CHANGE)
            for (String phase : TT_PHASES) {
                if (!uploadedPhotos.contains(phase)) return phase;
            }
        else {
            if (!uploadedPhotos.contains("демонтирован")) return "демонтирован";
            if (!uploadedPhotos.contains("установлен")) return "установлен";
        }
        return null; // Все фото загружены
    }
}

