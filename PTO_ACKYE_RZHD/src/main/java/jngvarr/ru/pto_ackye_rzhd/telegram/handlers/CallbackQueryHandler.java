package jngvarr.ru.pto_ackye_rzhd.telegram.handlers;

import jngvarr.ru.pto_ackye_rzhd.application.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.telegram.TBot;
import jngvarr.ru.pto_ackye_rzhd.domain.value.OtoType;
import jngvarr.ru.pto_ackye_rzhd.domain.value.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.application.services.TBotConversationStateService;
import jngvarr.ru.pto_ackye_rzhd.application.util.TBotConversationUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.COMPLETE_BUTTON;

@Slf4j
@Component
@Data
@RequiredArgsConstructor
public class CallbackQueryHandler {

    private final TBot tBot;
    private final TBotConversationStateService conversationStateService;
    private final ExcelFileService excelFileService;
    private final TBotConversationUtils conversationUtils;

    public void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long userId = update.getCallbackQuery().getFrom().getId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (callbackData) {
            case "mount" -> {
                tBot.editTextAndButtons(NEW_TU, MODES.get(callbackData), chatId, userId, 1);
//                sendTextMessage(NEW_TU, modes.get(callbackData), chatId, 1);
            }
            case "pto" -> {
                conversationStateService.setPtoFlags(userId, true);
                tBot.editTextAndButtons(PTO, MODES.get(callbackData), chatId, userId, 2);
//                sendTextMessage(PTO, modes.get(callbackData), chatId, 2);
            }
            case "oto" -> {
//                sendTextMessage(OTO, modes.get(callbackData), chatId, 2);
                tBot.editTextAndButtons(OTO, MODES.get(callbackData), chatId, userId, 2);
            }

            // Обработка выбора для ПТО счетчика и концентратора
            case "ptoIIK", "ptoIVKE" -> {
                String textToSend;
                if ("ptoIIK".equals(callbackData)) {
                    textToSend = "Пожалуйста, загрузите фото счетчика и введите показания.";
                    conversationStateService.setProcessState(userId, ProcessState.WAITING_FOR_METER_PHOTO);
                } else {
                    textToSend = "Пожалуйста, загрузите фото концентратора и введите его номер.";
                    conversationStateService.setProcessState(userId, ProcessState.WAITING_FOR_DC_PHOTO);
                }
                tBot.editMessage(chatId, userId, textToSend);
            }

            case "otoIIK", "otoIVKE" -> {
                if (callbackData.equals("otoIIK")) {
                    tBot.editTextAndButtons("Выберите вид ОТО ИИК: ", OTO_IIK_BUTTONS, chatId, userId, 2);
                    conversationStateService.setProcessState(userId, ProcessState.IIK_WORKS);
                } else {
                    tBot.editTextAndButtons("Выберите вид ОТО ИВКЭ: ", OTO_IVKE_BUTTONS, chatId, userId, 2);
                    conversationStateService.setProcessState(userId, ProcessState.DC_WORKS);
                }
            }

            case "wkDrop", "setNot", "powerSupplyRestoring", "dcRestart" -> {
                switch (callbackData) {
                    case "wkDrop" -> {
                        tBot.editMessage(chatId, userId, "Введите номер прибора учета: ");
                        conversationStateService.setOtoType(userId, OtoType.WK_DROP);
                    }
                    case "dcRestart" -> {
                        tBot.editMessage(chatId, userId, "Введите номер концентратора: ");
                        conversationStateService.setOtoType(userId, OtoType.DC_RESTART);
                    }
                    case "setNot" -> {
                        String textToSend = conversationStateService.getProcessState(userId).equals(ProcessState.IIK_WORKS) ?
                                "Введите номер прибора учета: " : "Введите номер концентратора: ";
                        tBot.editMessage(chatId, userId, textToSend);
                        conversationStateService.setOtoType(userId, OtoType.SET_NOT);
                    }
                    default -> {
                        String textToSend = conversationStateService.getProcessState(userId).equals(ProcessState.IIK_WORKS) ?
                                "Введите номер прибора учета: " : "Введите номер концентратора: ";
                        tBot.editMessage(chatId, userId, textToSend);
                        conversationStateService.setOtoType(userId, OtoType.SUPPLY_RESTORING);
                    }
                }
            }

            case "meterChange", "ttChange", "dcChange" -> {
                String value1 = "";
                String value2 = "";
                if (callbackData.equals("meterChange")) {
                    value1 = "meterChangeWithPhoto";
                    value2 = "meterChangeWithoutPhoto";
                } else if ("ttChange".equals(callbackData)) {
                    value1 = "ttChangeWithPhoto";
                    value2 = "ttChangeWithoutPhoto";
                } else {
                    value1 = "dcChangeWithPhoto";
                    value2 = "dcChangeWithoutPhoto";
                }
                tBot.editTextAndButtons("Вид передачи данных: ",
                        Map.of("С приложением фото.", value1,
                                "Без приложения фото.", value2), chatId, userId, 2);
            }

            case "ttChangeWithPhoto", "ttChangeWithoutPhoto" -> {
                if ("ttChangeWithPhoto".equals(callbackData))
                    conversationStateService.setProcessState(userId, ProcessState.WAITING_FOR_TT_PHOTO);
                tBot.editMessage(chatId, userId, "Введите номер прибора учета: ");
                conversationStateService.setOtoType(userId, OtoType.TT_CHANGE);
            }

            case "dcChangeWithPhoto", "dcChangeWithoutPhoto" -> {
                String textToSend = "";
                conversationStateService.setOtoType(userId, OtoType.DC_CHANGE);
                if ("dcChangeWithPhoto".equals(callbackData)) {
                    conversationStateService.setProcessState(userId, ProcessState.WAITING_FOR_DC_PHOTO);
                    textToSend = "Загрузите фото демонтируемого концентратора и введите его номер: ";
                } else textToSend = "Введите номер демонтируемого концентратора: ";
                tBot.editMessage(chatId, userId, textToSend);
            }

            case "meterChangeWithPhoto", "meterChangeWithoutPhoto" -> {
                String textToSend = "";
                conversationStateService.setOtoType(userId, OtoType.METER_CHANGE);

                if ("meterChangeWithPhoto".equals(callbackData)) {
                    textToSend = "📸 Пожалуйста, загрузите фото **ДЕМОНТИРОВАННОГО** прибора и введите показания.";
                    conversationStateService.setProcessState(userId, ProcessState.WAITING_FOR_METER_PHOTO);
                } else textToSend = "Введите номер демонтируемого прибора учета: ";
                tBot.editMessage(chatId, userId, textToSend);
            }

            case "LOADING_COMPLETE" -> {
                if (conversationStateService.getPtoFlag(userId)) {
                    conversationStateService.clearUserData(userId);
                    tBot.sendMessage(chatId, userId, "Для продолжения снова нажмите /start");
                } else {
                    tBot.editTextAndButtons(conversationUtils.actionConfirmation(userId), CONFIRM_MENU, chatId, userId, 2);
                }
            }

            case "confirm", "cancel" -> {
                String textToSend;
                if ("confirm".equals(callbackData)) {
                    textToSend = "Информация сохранена.";
                    tBot.sendMessage(chatId, userId, "Подождите, идёт загрузка данных...");
                    excelFileService.sheetsFilling(userId);
                } else {
                    textToSend = "Информация не сохранена.";
                }
                tBot.editMessage(chatId, userId, textToSend);
                conversationStateService.clearUserData(userId);
                tBot.sendMessage(chatId, userId, "Для продолжения снова нажмите /start");
            }

            case "NOT", "lowPLC", "NOT3", "NOT2", "seasonNOT", "NOT1" -> {
                conversationStateService.appendProcessInfo(chatId, GET_STRING_NOT.get(callbackData));
                conversationUtils.formingOtoLog(conversationStateService.getProcessInfo(userId), OtoType.SET_NOT, userId);
                tBot.editTextAndButtons("Введите номер следующего ПУ или закончите ввод.", COMPLETE_BUTTON, chatId, userId, 1);
            }
            case "iikMount", "dcMount" -> {
                String textToSend = "";
                if ("iikMount".equals(callbackData)) {
                    textToSend = " номер концентратора, к которому привязан ИИК (если номер не известен - введите \\\"0\\\"): \"";
                    conversationStateService.setProcessState(userId, ProcessState.IIK_MOUNT);
                } else {
                    textToSend = "наименование станции";
                    conversationStateService.setProcessState(userId, ProcessState.DC_MOUNT);
                }
                tBot.editMessage(chatId, userId, "Введите " + textToSend + ": ");
            }
            default -> tBot.sendMessage(chatId, userId, "Неизвестное действие. Попробуйте еще раз.");
        }
    }
}
