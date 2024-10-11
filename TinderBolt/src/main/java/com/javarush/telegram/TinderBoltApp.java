package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "ThunderBot_AI_bot"; //TODO: добавь имя бота в кавычках
    public static final String TELEGRAM_BOT_TOKEN = "7378649058:AAEB4Yznyde-uZTg9DPLDkKtwgzPhJPJokM"; //TODO: добавь токен бота в кавычках
    public static final String OPEN_AI_TOKEN = "gpt:FUj5RRFaxyJPx8r99jGlJFkblB3TiwFfStVTSk3zPf89Bss9"; //TODO: добавь токен ChatGPT в кавычках
    private final ChatGPTService chatGPTService = new ChatGPTService(OPEN_AI_TOKEN);
    private DialogMode currentMode = null;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    private ArrayList<String> list = new ArrayList<>();

    private UserInfo me;
    private UserInfo she;
    private int questionCount;

    @Override
    public void onUpdateEventReceived(Update update) {
        //TODO: основной функционал бота будем писать здесь

        String msg = getMessageText();

        if (msg.equals("/start")) {
            currentMode = DialogMode.MAIN;
            sendPhotoMessage("main");
            String text = loadMessage("main");
            sendTextMessage("Привет-привет!!!");
            sendTextMessage(text);

            showMainMenu("Начало", "/start",
                    "генерация Tinder-профля 😎", "/profile",
                    "сообщение для знакомства \uD83E\uDD70", "/opener",
                    "переписка от вашего имени \uD83D\uDE08", "/message",
                    "переписка со звездами \uD83D\uDD25", "/date",
                    "Общение с chatGPT  \uD83E\uDDE0", "/gpt"
            );
            return;
        }

        if (msg.equals("/gpt")) {
            currentMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            String text = loadMessage("gpt");
//            sendTextMessage("Напишите сообщение gpt-чату: ");
            sendTextMessage(text);
            return;
        }

        if (currentMode == DialogMode.GPT && !isMessageCommand()) {
            String prompt = loadPrompt("gpt");
            Message msg2 = sendTextMessage("Ожидайте, chatGPT задумался...");
            String answer = chatGPTService.sendMessage(prompt, msg);
            updateTextMessage(msg2, answer);
            return;
        }

        if (msg.equals("/date")) {
            currentMode = DialogMode.DATE;
            sendPhotoMessage("date");
            String text = loadMessage("date");
            sendTextButtonsMessage(text,
                    "Ариана Гранде", "date_grande",
                    "Марго Робби", "date_grande",
                    "Зендея", "date_zendaya",
                    "Райн Гослинг", "date_gosling",
                    "Том Харди", "date_hardy");
            return;
        }

        if (currentMode == DialogMode.DATE && !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("date_")) {
                sendPhotoMessage(query);
                sendTextMessage(" Отличный выбор, \uD83D\uDE05 \n*Вы должны пригласить девушку/парня на свидание ❤\uFE0F за пять сообщений.* \nПервый шаг за вами: ");
                chatGPTService.setPrompt(loadPrompt(query));
                return;
            }
            Message msg2 = sendTextMessage("Собеседник набирает текст...");
            updateTextMessage(msg2, chatGPTService.addMessage(msg));
            return;
        }
        if (msg.equals("/message")) {
            currentMode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            sendTextButtonsMessage("Пришлите вашу переписку",
                    "Следующее сообщение", "message_next",
                    "Пригласить на свидание", "message_date");
            return;
        }

        if (currentMode == DialogMode.MESSAGE && !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("message_")) {
                String prompt = loadPrompt(query);
                String userHistory = String.join("\n\n", list);

                Message msg2 = sendTextMessage("Ожидайте, chatGPT задумался...");
                String answer = chatGPTService.sendMessage(prompt, userHistory);

                updateTextMessage(msg2, answer);
                return;
            }
            list.add(msg);
            return;
        }

        if (msg.equals("/profile")) {
            currentMode = DialogMode.PROFILE;
            sendPhotoMessage("profile");
            me = new UserInfo();
            sendTextMessage("Сколько вам лет?");
            return;
        }

        if (currentMode == DialogMode.PROFILE && !isMessageCommand()) {
            questionCount++;
            switch (questionCount) {
                case 1:
                    me.age = msg;
                    sendTextMessage("Кем вы работаете?");
                    return;
                case 2:
                    me.occupation = msg;
                    sendTextMessage("Какое у вас хобби?");
                    return;
                case 3:
                    me.hobby = msg;
                    sendTextMessage("Что вам НЕ нравится в людях?");
                    return;
                case 4:
                    me.annoys = msg;
                    sendTextMessage("Цель знакомства?");
                    return;
                case 5:
                    me.goals = msg;

                    String aboutMyself = me.toString();
                    String prompt = loadPrompt("profile");
                    Message msg2 = sendTextMessage("Ожидайте, chatGPT задумался...");
                    String answer = chatGPTService.sendMessage(prompt, aboutMyself);
                    updateTextMessage(msg2, answer);
                    return;
            }
            questionCount = 0;
            return;
        }

        if (msg.equals("/opener")) {
            currentMode = DialogMode.OPENER;
            sendPhotoMessage("opener");
            she = new UserInfo();
            sendTextMessage("Имя девушки?");
            return;
        }
        if (currentMode == DialogMode.OPENER && !isMessageCommand()) {
            questionCount++;
            switch (questionCount) {
                case 1:
                    she.name = msg;
                    sendTextMessage("Сколько ей лет?");
                    return;
                case 2:
                    she.age = msg;
                    sendTextMessage("Какое у нее хобби?");
                    return;
                case 3:
                    she.hobby = msg;
                    sendTextMessage("Кем она работает?");
                    return;
                case 4:
                    she.occupation = msg;
                    sendTextMessage("Цель знакомства?");
                    return;
                case 5:
                    she.goals = msg;

                    String aboutFriend = she.toString();
                    String prompt = loadPrompt("opener");
                    Message msg2 = sendTextMessage("Ожидайте, chatGPT задумался...");
                    String answer = chatGPTService.sendMessage(prompt, aboutFriend);
                    updateTextMessage(msg2, answer);
                    return;
            }
            questionCount = 0;
            return;
        }
        sendTextMessage("*Hello!*");
        sendTextMessage("_Hello!_");
        sendTextMessage("Вы писали: " + msg);

        sendTextButtonsMessage("Выберите режим работы: ", "Старт", "start", "Стоп", "stop");

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
