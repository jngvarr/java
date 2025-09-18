package jngvarr.ru.pto_ackye_rzhd.application.services.dialog;

import jngvarr.ru.pto_ackye_rzhd.application.management.MeterManagementService;
import jngvarr.ru.pto_ackye_rzhd.application.services.PhotoPathService;
import jngvarr.ru.pto_ackye_rzhd.application.services.TBotConversationStateService;
import jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils;
import jngvarr.ru.pto_ackye_rzhd.application.util.TBotConversationUtils;
import jngvarr.ru.pto_ackye_rzhd.domain.value.OtoType;
import jngvarr.ru.pto_ackye_rzhd.domain.value.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.telegram.TBotMessageService;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;


@Data
@Component
public class EquipmentManipulationsService {
    private final TBotMessageService tBotMessageService;
    private final TBotConversationStateService conversationStateService;
    private final PhotoPathService photoPathService;
    private final DateUtils dateUtils;
    private final TBotConversationUtils conversationUtils;

    public void handleEquipmentChange(long userId, long chatId, String msgText, OtoType otoType, ProcessState processState) {
        Map<Integer, String> replacedEquipmentData = REPLACED_EQUIPMENT_DATUM.get(otoType);
        int sequenceNumber = conversationStateService.getSequenceNumber(chatId);
        if (msgText != null && !msgText.trim().isEmpty()) {
            conversationStateService.appendProcessInfo(chatId, msgText + "_");
        }
        if (sequenceNumber < replacedEquipmentData.size()) {
            if (processState.equals(ProcessState.WAITING_FOR_TT_PHOTO) && sequenceNumber == 4) {
                tBotMessageService.sendMessage(chatId, userId, "📸 Прикрепите фото **ТТ фазы A** и введите его номер:");
            } else {
                tBotMessageService.sendMessage(chatId, userId, replacedEquipmentData.get(sequenceNumber));
            }
            conversationStateService.incrementSequenceNumber(userId);
        } else concludeEquipmentOperation(userId, chatId, processState);
    }

    public void handleEquipmentMount(long userId, long chatId, String msgText, ProcessState processState) {
        int sequenceNumber = conversationStateService.getSequenceNumber(chatId);
        Map<Integer, String> mountedEquipmentData = MOUNTED_EQUIPMENT_DATUM.get(processState);
        boolean hasWrongInput = false;
        if (msgText != null && !msgText.trim().isEmpty()) {

            // Проверка типа прибора учета
            if (ProcessState.IIK_MOUNT.equals(processState)
                    && sequenceNumber == 4
                    && MeterManagementService.getMeterTypes().contains(msgText.toUpperCase())) {

                tBotMessageService.sendMessage(chatId, userId, "Тип прибора учета указан неверно!!!");
                sequenceNumber--;
                hasWrongInput = true;
            }

            // Для всех остальных шагов (кроме даты на шаге 9) добавляем текст как есть
            if (ProcessState.IIK_MOUNT.equals(processState)
                    && sequenceNumber != 10
                    && !hasWrongInput) {
                conversationStateService.appendProcessInfo(chatId, msgText + "_");
            }

            // Обработка по шагам
            if (ProcessState.IIK_MOUNT.equals(processState)) {
                switch (sequenceNumber) {
                    case 0 -> {
                        String path = photoPathService.getSavingPaths().get(msgText);
                        if (path != null) {
                            conversationStateService.increaseSequenceNumber(chatId, 2);
                            String[] pathParts = path.split("\\\\");
                            conversationStateService.appendProcessInfo(userId, pathParts[pathParts.length - 2] + "_" + pathParts[pathParts.length - 1] + "_");
                        }
                    }
                    case 10 -> {
                        // Нормализация и проверка даты
                        String normalizedDate = dateUtils.normalizeDate(msgText);
                        if (normalizedDate == null) {
                            tBotMessageService.sendMessage(chatId, userId, "Формат даты указан неверно!!!");
                            conversationStateService.decrementSequenceNumber(userId);
                        } else {
                            conversationStateService.appendProcessInfo(userId, normalizedDate + "_"); // сохраняем уже нормализованную дату
                        }
                    }
                }
            }
        }

        // Следующий шаг или завершение процесса
        if (sequenceNumber < mountedEquipmentData.size()) {
            sequenceNumber++;
            tBotMessageService.sendMessage(chatId, userId, mountedEquipmentData.get(sequenceNumber));
        } else {
            concludeEquipmentOperation(userId, chatId, processState);
        }
    }

    public void concludeEquipmentOperation(long userId, long chatId, ProcessState processState) {
        OtoType otoType = conversationStateService.getOtoType(userId);
        Object typeIndicator = otoType != null ? otoType : processState;
        conversationUtils.formingOtoLog(conversationStateService.getProcessInfo(userId), typeIndicator, userId);
        tBotMessageService.editTextAndButtons(conversationUtils.actionConfirmation(null), CONFIRM_MENU, chatId, userId, 2);
    }
}
