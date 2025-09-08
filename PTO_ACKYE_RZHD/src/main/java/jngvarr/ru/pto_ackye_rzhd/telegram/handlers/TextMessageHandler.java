package jngvarr.ru.pto_ackye_rzhd.telegram.handlers;

import jngvarr.ru.pto_ackye_rzhd.application.management.MeterManagementService;
import jngvarr.ru.pto_ackye_rzhd.domain.value.OtoType;
import jngvarr.ru.pto_ackye_rzhd.domain.value.PendingPhoto;
import jngvarr.ru.pto_ackye_rzhd.domain.value.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.telegram.TBot;
import jngvarr.ru.pto_ackye_rzhd.application.services.TBotConversationStateService;
import jngvarr.ru.pto_ackye_rzhd.application.util.TBotConversationUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.CONFIRM_MENU;

@Data
@Component
@Slf4j
@RequiredArgsConstructor
public class TextMessageHandler {

    private final TBot tBot;
    private final PhotoMessageHandler photoMessageHandler;
    private final TBotConversationUtils conversationUtils;
    private final TBotConversationStateService conversationStateService;

    public void handleTextMessage(Update update) {
        String msgText = update.getMessage().getText();
        long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();

        ProcessState processState = conversationStateService.getProcessState(userId);
        OtoType otoType = conversationStateService.getOtoType(userId);


        switch (msgText) {
            case "/start" -> {
                handleStartCommand(chatId, userId);
                clearData();
                return;
            }
            case "/help" -> {
                tBot.sendMessage(chatId, userId, HELP);
                return;
            }
//            case "/register" -> {
//                conversationStateService.setProcessStates(userId, ProcessState.REGISTRATION);
//                registerUser(update);
//                clearData();
//                return;
//            }
            case "/stop" -> {
                tBot.sendMessage(chatId, userId, "Работа прервана, для продолжения нажмите /start");
                clearData();
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
                    handleManualInsert(userId, chatId, msgText);
                    return;
                }
                case IIK_MOUNT, DC_MOUNT -> {
                    handleEquipmentMount(userId, chatId, msgText, processState);
                    return;
                }
            }
        }
        if (otoType != null) {
            switch (otoType) {
                case TT_CHANGE, METER_CHANGE, DC_CHANGE -> {
                    handleEquipmentChange(userId, chatId, msgText, otoType);
                    return;
                }
                case WK_DROP, SET_NOT, SUPPLY_RESTORING, DC_RESTART -> {
                    handleOtherOtoTypes(userId, chatId, msgText);
                    return;
                }
            }
        }
        tBot.sendMessage(chatId, userId, "Команда не распознана. Попробуйте еще раз.");

    }

    private void handleEquipmentMount(long userId, long chatId, String msgText, ProcessState state) {
        Map<Integer, String> mountedEquipmentData = MOUNTED_EQUIPMENT_DATUM.get(state);
        boolean hasWrongInput = false;
        if (msgText != null && !msgText.trim().isEmpty()) {

            // Проверка типа прибора учета
            if (ProcessState.IIK_MOUNT.equals(processStates.get(userId))
                    && sequenceNumber == 4
                    && MeterManagementService.getMeterTypes().contains(msgText.toUpperCase())) {

                tBot.sendMessage(chatId, userId, "Тип прибора учета указан неверно!!!");
                sequenceNumber--;
                hasWrongInput = true;
            }

            // Для всех остальных шагов (кроме даты на шаге 9) добавляем текст как есть
            if (ProcessState.IIK_MOUNT.equals(processStates.get(userId))
                    && sequenceNumber != 10
                    && !hasWrongInput) {
                processInfo += msgText + "_";
            }

            // Обработка по шагам
            if (ProcessState.IIK_MOUNT.equals(processStates.get(userId))) {
                switch (sequenceNumber) {
                    case 0 -> {
                        String path = stringUtils.getSavingPaths().get(msgText);
                        if (path != null) {
                            sequenceNumber += 2;
                            addPathToProcessInfo(path);
                        }
                    }
                    case 10 -> {
                        // Нормализация и проверка даты
                        String normalizedDate = dateUtils.normalizeDate(msgText);
                        if (normalizedDate == null) {
                            tBot.sendMessage(chatId, userId, "Формат даты указан неверно!!!");
                            sequenceNumber--;
                        } else {
                            processInfo += normalizedDate + "_"; // сохраняем уже нормализованную дату
                        }
                    }
                }
            }
        }

        // Следующий шаг или завершение процесса
        if (sequenceNumber < mountedEquipmentData.size()) {
            tBot.sendMessage(chatId, userId, mountedEquipmentData.get(sequenceNumber));
            sequenceNumber++;
        } else {
            concludeDeviceOperation(userId, chatId);
        }
    }

    private void handleStartCommand(long chatId, long userId) {
        tBot.sendTextMessage(MAIN_MENU, START_MENU_BUTTONS, chatId, userId, 1);
    }

    private void handleManualInsert(long userId, long chatId, String deviceNumber) {
        String manualInput = deviceNumber.trim();
        PendingPhoto pending = conversationStateService.getPendingPhoto(userId);
        if (pending != null) {
            if (conversationStateService.getProcessState(userId).equals(ProcessState.MANUAL_INSERT_METER_INDICATION)) {
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

    private void handleOtherOtoTypes(long userId, long chatId, String msgText) {
        OtoType currentOtoType = otoTypes.get(userId);
        String messageText = msgText.trim();

        switch (currentOtoType) {
            case WK_DROP -> {
                conversationStateService.getOtoLog().put(messageText, "WK_");
                tBot.editTextAndButtons("Введите номер следующего прибора учета или закончите ввод.", COMPLETE_BUTTON, chatId, userId, 1);
            }
            case SET_NOT -> {
                processInfo += msgText + "_";
                if (sequenceNumber == 0) {
                    if (ProcessState.DC_WORKS.equals(processStates.get(userId))) {
                        tBot.sendMessage(chatId, userId, "Введите причину отключения: ");
                    } else {
                        chooseNotType(chatId, userId);
                    }
                    sequenceNumber++;
                } else {
                    conversationUtils.formingOtoLog(conversationStateService.getProcessInfo(userId), currentOtoType, userId);
                    tBot.sendTextMessage(tBotService.actionConfirmation(userId), CONFIRM_MENU, chatId, userId, 2);
                }
            }
            case SUPPLY_RESTORING, DC_RESTART -> {
                if (currentOtoType.equals(OtoType.SUPPLY_RESTORING)) {
                    processInfo += msgText + "_";
                    if (sequenceNumber == 0) {
                        tBot.sendMessage(chatId, userId, "Опишите причину неисправности: ");
                        sequenceNumber++;
                    } else {
                        tBotService.formingOtoLog(processInfo, currentOtoType);
                        tBot.sendTextMessage(tBotService.actionConfirmation(userId), CONFIRM_MENU, chatId, userId, 2);
                    }
                } else {
                    conversationStateService.getOtoLog().put(messageText, "dcRestart_");
                    tBot.sendTextMessage(tBotService.actionConfirmation(userId), CONFIRM_MENU, chatId, userId, 2);
                }
            }
        }
    }

    private void handleEquipmentChange(long userId, long chatId, String msgText, OtoType otoType) {
        Map<Integer, String> replacedEquipmentData = REPLACED_EQUIPMENT_DATUM.get(otoType);
        if (msgText != null && !msgText.trim().isEmpty()) {
            processInfo += msgText + "_";
        }
        if (sequenceNumber < replacedEquipmentData.size()) {
            if (processStates.get(userId).equals(TBot.ProcessState.WAITING_FOR_TT_PHOTO) && sequenceNumber == 4) {
                tBot.sendMessage(chatId, userId, "📸 Прикрепите фото **ТТ фазы A** и введите его номер:");
            } else {
                tBot.sendMessage(chatId, userId, replacedEquipmentData.get(sequenceNumber));
            }
            sequenceNumber++;
        } else concludeDeviceOperation(userId, chatId);
    }


    private void addPathToProcessInfo(String path) {
        String[] pathParts = path.split("\\\\");
        processInfo = processInfo + pathParts[pathParts.length - 2] + "_" + pathParts[pathParts.length - 1] + "_";
        isDcLocation = true;
    }


    private void concludeDeviceOperation(long userId, long chatId) {
        OtoType otoType = otoTypes.get(userId);
        Object typeIndicator = otoType != null ? otoType : processStates.get(userId);
        formingOtoLog(processInfo, typeIndicator);
        tBot.editTextAndButtons(actionConfirmation(null), CONFIRM_MENU, chatId, userId, 2);
    }


    private void chooseNotType(Long chatId, Long userId) {
        tBot.sendTextMessage("Выберите причину отключения: ", DISCONNECT_REASON, chatId, userId, 1);
    }


    public void clearData() {
        otoLog.clear();
        sequenceNumber = 0;
        processStates.clear();
        otoTypes.clear();
        processInfo = "";
        isPTO = false;
    }

}
