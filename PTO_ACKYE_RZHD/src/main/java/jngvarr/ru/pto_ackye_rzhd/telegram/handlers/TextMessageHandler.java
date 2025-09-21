package jngvarr.ru.pto_ackye_rzhd.telegram.handlers;

import jngvarr.ru.pto_ackye_rzhd.application.services.TBotConversationStateService;
import jngvarr.ru.pto_ackye_rzhd.application.services.dialog.EquipmentManipulationsService;
import jngvarr.ru.pto_ackye_rzhd.application.services.dialog.ManualInsertService;
import jngvarr.ru.pto_ackye_rzhd.application.services.dialog.OtherOtoWorksService;
import jngvarr.ru.pto_ackye_rzhd.domain.value.OtoType;
import jngvarr.ru.pto_ackye_rzhd.domain.value.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.telegram.service.TBotMessageService;
import jngvarr.ru.pto_ackye_rzhd.telegram.UpdateHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;

@Data
@Slf4j
@Component
public class TextMessageHandler implements UpdateHandler {

    private final TBotMessageService tBotMessageService;
    private final PhotoMessageHandler photoMessageHandler;
    private final TBotConversationStateService conversationStateService;
    private final EquipmentManipulationsService equipmentManipulationsService;
    private final ManualInsertService manualInsertService;
    private final OtherOtoWorksService otherOtoWorksService;

    @Override
    public void handle(Update update) {
        String msgText = update.getMessage().getText();
        long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();

        ProcessState processState = conversationStateService.getProcessState(userId);
        OtoType otoType = conversationStateService.getOtoType(userId);


        switch (msgText) {
            case "/start" -> {
//                tBotMessageService.sendMessage(chatId, userId, INTRO);
                tBotMessageService.sendTextMessage(MAIN_MENU, START_MENU_BUTTONS, chatId, userId, 1);
                conversationStateService.clearUserData(userId);
                return;
            }
            case "/help" -> {
                tBotMessageService.sendMessage(chatId, userId, HELP);
                return;
            }
            case "/register" -> {
                tBotMessageService.sendMessage(chatId, userId,
                        "Вы уже зарегистрированы!!!\n" +
                                "Чтобы продолжить работу нажмите /start ");
//                conversationStateService.setProcessStates(userId, ProcessState.REGISTRATION);
//                registerUser(update);
//                clearData();
                return;
            }
            case "/stop" -> {
                tBotMessageService.sendMessage(chatId, userId, "Работа прервана, для продолжения нажмите /start");
                conversationStateService.clearUserData(userId);
                return;
            }
//            case "/accept" -> {
//
//                sendMessage(chatId, "Работа прервана, для продолжения нажмите /start");
//                clearData();
//                return;
//            }
        }

        if (processState != null) {
            switch (processState) {
                case MANUAL_INSERT_METER_NUMBER, MANUAL_INSERT_METER_INDICATION -> {
                    manualInsertService.handleManualInsert(userId, chatId, msgText, photoMessageHandler);
                    return;
                }
                case IIK_MOUNT, DC_MOUNT -> {
                    equipmentManipulationsService.handleEquipmentMount(userId, chatId, msgText, processState);
                    return;
                }
            }
        }
        if (otoType != null) {
            switch (otoType) {
                case TT_CHANGE, METER_CHANGE, DC_CHANGE -> {
                    equipmentManipulationsService.handleEquipmentChange(userId, chatId, msgText, otoType, processState);
                    return;
                }
                case WK_DROP, SET_NOT, SUPPLY_RESTORING, DC_RESTART -> {
                    otherOtoWorksService.handleOtherOtoTypes(userId, chatId, msgText);
                    return;
                }
            }
        }
        tBotMessageService.sendMessage(chatId, userId, "Команда не распознана. Попробуйте еще раз.");
    }

    @Override
    public boolean canHandle(Update update) {
        return update.getMessage().hasText();
    }
}
