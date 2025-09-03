package jngvarr.ru.pto_ackye_rzhd.telegram.services;

import jngvarr.ru.pto_ackye_rzhd.telegram.domain.OtoType;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.PendingPhoto;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.PhotoState;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.ProcessState;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Data
@Service
@RequiredArgsConstructor
public class TBotConversationStateService {

    // Мапа для хранения состояния диалога по userId
    private Map<Long, ProcessState> processStates = new HashMap<>();
    private Map<Long, OtoType> otoTypes = new HashMap<>();
    private Map<String, String> otoLog = new HashMap<>();
    private final Map<Long, Integer> sequenceNumbers = new HashMap<>();
    private final Map<Long, String> processInfos = new HashMap<>();
    private final Map<Long, Boolean> ptoFlags = new HashMap<>();
    private final Map<Long, Boolean> dcLocationFlags = new HashMap<>();
    private Map<Long, PendingPhoto> pendingPhotos = new HashMap<>();
    private Map<Long, PhotoState> photoStates = new HashMap<>();

    public ProcessState getProcessState(Long userId) {
        return processStates.get(userId);
    }

    public void setProcessState(Long userId, ProcessState state) {
        processStates.put(userId, state);
    }

    public void clearProcessState(Long userId) {
        processStates.remove(userId);
    }

    public OtoType getOtoType(Long userId) {
        return otoTypes.get(userId);
    }

    public void setOtoType(Long userId, OtoType type) {
        otoTypes.put(userId, type);
    }

    public int getSequenceNumber(Long userId) {
        return sequenceNumbers.getOrDefault(userId, 0);
    }

    public void setSequenceNumber(Long userId, int sequenceNumber) {
        sequenceNumbers.put(userId, sequenceNumber);
    }

    public void incrementSequenceNumber(Long userId) {
        sequenceNumbers.put(userId, getSequenceNumber(userId) + 1);
    }

    public void resetSequence(Long userId) {
        sequenceNumbers.put(userId, 0);
    }

    public String getProcessInfo(Long userId) {
        return processInfos.getOrDefault(userId, "");
    }

    public void appendProcessInfo(Long userId, String value) {
        processInfos.put(userId, getProcessInfo(userId) + value);
    }

    public void setPendingPhoto(Long userId, PendingPhoto photo) {
        pendingPhotos.put(userId, photo);
    }

    public PendingPhoto getPendingPhoto(Long userId) {
        return pendingPhotos.get(userId);
    }

    public void clearPendingPhoto(Long userId) {
        pendingPhotos.remove(userId);
    }

    public void setPhotoState(Long userId, PhotoState state) {
        photoStates.put(userId, state);
    }

    public PhotoState getPhotoState(Long userId) {
        return photoStates.get(userId);
    }

    public void clearPhotoState(Long userId) {
        photoStates.remove(userId);
    }

    public void clearUserData(Long userId) {
        processStates.remove(userId);
        otoTypes.remove(userId);
        otoLog.clear();
        sequenceNumbers.remove(userId);
        processInfos.remove(userId);
        ptoFlags.remove(userId);
        dcLocationFlags.remove(userId);
    }

}
