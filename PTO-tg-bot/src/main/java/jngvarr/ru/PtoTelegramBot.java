package jngvarr.ru;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

import static jngvarr.ru.PtoTelegramBotContent.*;


public class    PtoTelegramBot extends MultiSessionTelegramBot {
    public static final String NAME = "pto_ackye_bot"; // TODO: добавьте имя бота в кавычках
    public static final String TOKEN = "7646786291:AAHNu83gn4yDvxPupN_yB5GQ_sc44o-oKUw"; //TODO: добавьте токен бота в кавычках

    public PtoTelegramBot() {
        super(NAME, TOKEN);
    }

    private WorkTypes currentMode = null;

    @Override
    public void onUpdateEventReceived(Update updateEvent) {
        String msg = getMessageText();
        logger.info("user name {}", getUsername(updateEvent));

        if (msg.contains("/start")) {
            sendPhotoMessageAsync("echelon1");
            sendPhotoMessageAsync("sts");
            sendTextMessageAsync(MAIN_MENU,
                    Map.of("ПТО", "pto", "ОТО", "oto", "Монтаж новой ТУ", "newTU"));
        }

        if (getCallbackQueryButtonKey().equals("pto")) {
            currentMode = WorkTypes.PTO;
            sendTextMessageAsync(PTO);
            handlePhotoMessage(updateEvent.getCallbackQuery().getMessage());
        } else if (getCallbackQueryButtonKey().equals("oto")) {
            currentMode = WorkTypes.OTO;
            sendTextMessageAsync(OTO, Map.of("Сброс WrongKey", "wk", "Замена счетчика", "meterChange", "Замена концентратора", "dcChange"));
        } else if (getCallbackQueryButtonKey().equals("newTU")) {
            currentMode = WorkTypes.NEW_TU;
            sendTextMessageAsync(NEW_TU);
        }
    }


    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new PtoTelegramBot());
            logger.info("Bot успешно запущен!");
        } catch (Exception e) {
            logger.error("Ошибка при запуске бота: ", e);
        }
    }
}

