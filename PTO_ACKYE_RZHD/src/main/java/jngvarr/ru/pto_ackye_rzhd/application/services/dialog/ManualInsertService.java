package jngvarr.ru.pto_ackye_rzhd.application.services.dialog;

import jngvarr.ru.pto_ackye_rzhd.application.services.TBotConversationStateService;
import jngvarr.ru.pto_ackye_rzhd.domain.value.PendingPhoto;
import jngvarr.ru.pto_ackye_rzhd.domain.value.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.telegram.TBot;
import jngvarr.ru.pto_ackye_rzhd.telegram.handlers.PhotoMessageHandler;
import org.springframework.stereotype.Component;

@Component
public class ManualInsertService {
    TBotConversationStateService conversationStateService;
    TBot tBot;
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
                tBot.sendMessage(chatId, userId, "Заводской номер не найден. Введите номер вручную:");
                conversationStateService.setProcessState(userId, ProcessState.MANUAL_INSERT_METER_NUMBER);
            } else {
                tBot.sendMessage(chatId, userId, "Показания счетчика не введены. Введите показания счётчика:");
                conversationStateService.setProcessState(userId, ProcessState.MANUAL_INSERT_METER_INDICATION);
            }
        } else {
            tBot.sendMessage(chatId, userId, "Ошибка: нет ожидающих фото для привязки показаний.");
        }
    }
}

