package jngvarr.ru.pto_ackye_rzhd.application.services.dialog;

import jngvarr.ru.pto_ackye_rzhd.application.services.TBotConversationStateService;
import jngvarr.ru.pto_ackye_rzhd.domain.value.PendingPhoto;
import jngvarr.ru.pto_ackye_rzhd.domain.value.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.telegram.TBotMessageService;
import jngvarr.ru.pto_ackye_rzhd.telegram.handlers.PhotoMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManualInsertService {
    private final TBotConversationStateService conversationStateService;
    private final TBotMessageService tBotMessageService;

    public void handleManualInsert(long userId, long chatId, String deviceNumber, PhotoMessageHandler photoMessageHandler) {
        String manualInput = deviceNumber.trim();
        PendingPhoto pending = conversationStateService.getPendingPhoto(userId);
        if (pending != null) {
            if ((ProcessState.MANUAL_INSERT_METER_INDICATION).equals(conversationStateService.getProcessState(userId))) {
                pending.setAdditionalInfo(manualInput);
            } else {
                pending.setDeviceNumber(manualInput);
            }
            boolean isDataFull = pending.getDeviceNumber() != null && pending.getAdditionalInfo() != null;
            if (isDataFull) {
                photoMessageHandler.savePhoto(userId, chatId, pending);
            } else if (pending.getDeviceNumber() == null) {
                tBotMessageService.sendMessage(chatId, userId, "Заводской номер не найден. Введите номер вручную:");
                conversationStateService.setProcessState(userId, ProcessState.MANUAL_INSERT_METER_NUMBER);
            } else {
                tBotMessageService.sendMessage(chatId, userId, "Показания счетчика не введены. Введите показания счётчика:");
                conversationStateService.setProcessState(userId, ProcessState.MANUAL_INSERT_METER_INDICATION);
            }
        } else {
            tBotMessageService.sendMessage(chatId, userId, "Ошибка: нет ожидающих фото для привязки показаний.");
        }
    }
}

