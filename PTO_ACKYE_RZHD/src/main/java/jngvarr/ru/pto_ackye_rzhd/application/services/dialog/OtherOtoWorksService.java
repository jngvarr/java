package jngvarr.ru.pto_ackye_rzhd.application.services.dialog;

import jngvarr.ru.pto_ackye_rzhd.application.services.TBotConversationStateService;
import jngvarr.ru.pto_ackye_rzhd.application.util.TBotConversationUtils;
import jngvarr.ru.pto_ackye_rzhd.domain.value.OtoType;
import jngvarr.ru.pto_ackye_rzhd.domain.value.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.telegram.service.TBotMessageService;
import lombok.Data;
import org.springframework.stereotype.Component;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;


@Data
@Component
public class OtherOtoWorksService {
    private final TBotConversationStateService conversationStateService;
    private final TBotConversationUtils conversationUtils;
    private final TBotMessageService tBotMessageService;

    public void handleOtherOtoTypes(long userId, long chatId, String msgText) {
        OtoType currentOtoType = conversationStateService.getOtoType(userId);
        int sequenceNumber = conversationStateService.getSequenceNumber(userId);
        String messageText = msgText.trim();

        switch (currentOtoType) {
            case WK_DROP -> {
                conversationStateService.getOtoLog().put(messageText, "WK_");
                tBotMessageService.editTextAndButtons("Введите номер следующего прибора учета или закончите ввод.", COMPLETE_BUTTON, chatId, userId, 1);
            }
            case SET_NOT -> {
                conversationStateService.appendProcessInfo(userId, msgText + "_");
                if (sequenceNumber == 0) {
                    if (ProcessState.DC_WORKS.equals(conversationStateService.getProcessState(userId))) {
                        tBotMessageService.sendMessage(chatId, userId, "Введите причину отключения: ");
                    } else {
                        tBotMessageService.sendTextMessage("Выберите причину отключения: ", DISCONNECT_REASON, chatId, userId, 1);
                    }
                    conversationStateService.incrementSequenceNumber(userId);
                } else {
                    conversationUtils.formingOtoLog(conversationStateService.getProcessInfo(userId), currentOtoType, userId);
                    tBotMessageService.sendTextMessage(conversationUtils.actionConfirmation(userId), CONFIRM_MENU, chatId, userId, 2);
                }
            }
            case SUPPLY_RESTORING, DC_RESTART -> {
                if (currentOtoType.equals(OtoType.SUPPLY_RESTORING)) {
                    conversationStateService.appendProcessInfo(userId, msgText + "_");
                    if (sequenceNumber == 0) {
                        tBotMessageService.sendMessage(chatId, userId, "Опишите причину неисправности: ");
                        conversationStateService.incrementSequenceNumber(userId);
                    } else {
                        conversationUtils.formingOtoLog(conversationStateService.getProcessInfo(userId), currentOtoType, userId);
                        tBotMessageService.sendTextMessage(conversationUtils.actionConfirmation(userId), CONFIRM_MENU, chatId, userId, 2);
                    }
                } else {
                    conversationStateService.getOtoLog().put(messageText, "dcRestart_");
                    tBotMessageService.sendTextMessage(conversationUtils.actionConfirmation(userId), CONFIRM_MENU, chatId, userId, 2);
                }
            }
        }
    }
}
