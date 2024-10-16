package com.javarush.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

import static com.javarush.telegrambot.TelegramBotContent.*;

public class MyFirstTelegramBot extends MultiSessionTelegramBot {
    public static final String NAME = "Another_JR_bot"; // TODO: добавьте имя бота в кавычках
    public static final String TOKEN = "7841453865:AAFHPv8SIhFijlP9AIZjEDZmo03sP0KCbyY"; //TODO: добавьте токен бота в кавычках

    public MyFirstTelegramBot() {
        super(NAME, TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update updateEvent) {
        // TODO: основной функционал бота будем писать здесь

        //отображаем сообщение о начале игры - нужно взломать холодильник
        if (getMessageText().equals("/start")) {
            setUserGlory(0);
            sendTextMessageAsync(STEP_1_TEXT,
                    Map.of("Взлом холодоса", "step_1_btn"));
        }
        if (getCallbackQueryButtonKey().equals("step_1_btn")) {
            addUserGlory(20);
            sendTextMessageAsync(STEP_2_TEXT,
                    Map.of("Взять сосиску + 20 к славе", "step_2_btn",
                            "Взять рыбу + 20 к славе", "step_2_btn",
                            "Скинуть банку с огурцами + 20 к славе", "step_2_btn"));
        }
        //взламываем робот-пылесос
        if (getCallbackQueryButtonKey().equals("step_2_btn")) {
            addUserGlory(20);
            sendTextMessageAsync(STEP_3_TEXT,
                    Map.of("Взлом робота-пылесоса", "step_3_btn"));
        }

        if (getCallbackQueryButtonKey().equals("step_3_btn")) {
            addUserGlory(20);
            sendTextMessageAsync(STEP_4_TEXT,
                    Map.of("Отправить пылесос за едой + 30 славы", "step_4_btn",
                            "Покататься на пылесосе + 30 славы", "step_4_btn",
                            "Убежать + 30 к славе", "step_4_btn"));
        }

        if (getCallbackQueryButtonKey().equals("step_4_btn")) {
            addUserGlory(30);
            sendTextMessageAsync(STEP_5_TEXT,
                    Map.of("Взять GoPro + 40 славы", "step_5_btn"));
        }

        if (getCallbackQueryButtonKey().equals("step_5_btn")) {
            addUserGlory(40);
            sendTextMessageAsync(STEP_6_TEXT,
                    Map.of("Снять видео!! + 30 к славе", "step_6_btn"));
        }

        //ломаем компьютер
        if (getCallbackQueryButtonKey().equals("step_6_btn")) {
//            addUserGlory(30);
            sendTextMessageAsync(STEP_7_TEXT,
                    Map.of("Взломать компьютер + 50 к славе", "step_7_btn"));
        }

        if (getCallbackQueryButtonKey().equals("step_7_btn")) {
            addUserGlory(50);
            sendTextMessageAsync(STEP_8_TEXT,
                    Map.of("Рассказать о своих достижениях! ", "step_8_btn"));
        }

        //хвастаемся
        if (getCallbackQueryButtonKey().equals("step_8_btn")) {
            addUserGlory(50);
            sendTextMessageAsync(FINAL_TEXT + "Накоплено славы: " + getUserGlory(),

                    Map.of("КОНЕЦ", "step_9_btn"));
        }

        if (getCallbackQueryButtonKey().equals("step_9_btn")) {
//            addUserGlory(50);
            sendTextMessageAsync("ИГРА ОКОНЧЕНА!");
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new MyFirstTelegramBot());
    }
}

//sendTextMessageAsync("Всем привет!");
//        if (getMessageText().contains("бомба")) {
//            sendTextMessageAsync("Опасноть!!!");
//        }
//
//        if (getMessageText().contains("картинка")) {
//            sendPhotoMessageAsync("step_8_pic");
//        }
//
//        if (getMessageText().contains("кот")) {
//            sendTextMessageAsync("выбери номер кота: ",
//                    Map.of("кот 1", "cat1", "кот 2", "cat2"));
//        }
//
//        if (getCallbackQueryButtonKey().equals("cat1")) {
//            sendPhotoMessageAsync("step_1_pic");
//        }
//        if (getCallbackQueryButtonKey().equals("cat2")) {
//            sendPhotoMessageAsync("step_2_pic");
//        }
//        if (getMessageText().equals("smile")) {
//            var message = getLastSentMessage();
//            editTextMessageAsync(message.getMessageId(), message.getText() + "😁");
//            sendPhotoMessageAsync("step_2_pic");
//        }
//👉 Допиши диалог. Если пользователь ввел команду "/bye", выведи сообщение "Hasta la vista, baby!"
//👉 Отобрази сообщение: "Ваше любимое животное?" с двумя кнопками: "Кот" и "Собака";
//👉 При нажатии пользователем кнопки "Кот", нужно вывести на экран картинку step_4_pic;
//👉 При нажатии пользователем кнопки "Собака", нужно вывести на экран картинку step_6_pic;
//
//        if (getMessageText().equals("/bye")) {
//            sendTextMessageAsync("Hasta la vista, baby!");
//        }
//
//        sendTextMessageAsync("Ваше любимое животное: ",
//                Map.of("кошка", "cat", "собакен", "dog"));
//
//        if (getCallbackQueryButtonKey().equals("cat")) {
//            sendPhotoMessageAsync("step_4_pic");
//        }
//        if (getCallbackQueryButtonKey().equals("dog")) {
//            sendPhotoMessageAsync("step_6_pic");
//        }