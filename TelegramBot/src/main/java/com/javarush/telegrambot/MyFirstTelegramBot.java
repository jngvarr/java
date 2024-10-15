package com.javarush.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

public class MyFirstTelegramBot extends MultiSessionTelegramBot {
    public static final String NAME = "Another_JR_bot"; // TODO: добавьте имя бота в кавычках
    public static final String TOKEN = "7841453865:AAFHPv8SIhFijlP9AIZjEDZmo03sP0KCbyY"; //TODO: добавьте токен бота в кавычках

    public MyFirstTelegramBot() {
        super(NAME, TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update updateEvent) {
        // TODO: основной функционал бота будем писать здесь

//        sendTextMessageAsync("Всем привет!");


        if (getMessageText().contains("бомба")) {
            sendTextMessageAsync("Опасноть!!!");
        }

        if (getMessageText().contains("картинка")) {
            sendPhotoMessageAsync("step_8_pic");
        }

        if (getMessageText().contains("кот")) {
            sendTextMessageAsync("выбери номер кота: ",
                    Map.of("кот 1", "cat1", "кот 2", "cat2"));
        }

        if (getCallbackQueryButtonKey().equals("cat1")) {
            sendPhotoMessageAsync("step_1_pic");
        }
        if (getCallbackQueryButtonKey().equals("cat2")) {
            sendPhotoMessageAsync("step_2_pic");
        }
        if (getMessageText().equals("smile")) {
            var message = getLastSentMessage();
            editTextMessageAsync(message.getMessageId(), message.getText() + "😁");
            sendPhotoMessageAsync("step_2_pic");
        }

//👉 Допиши диалог. Если пользователь ввел команду "/bye", выведи сообщение "Hasta la vista, baby!"
//👉 Отобрази сообщение: "Ваше любимое животное?" с двумя кнопками: "Кот" и "Собака";
//👉 При нажатии пользователем кнопки "Кот", нужно вывести на экран картинку step_4_pic;
//👉 При нажатии пользователем кнопки "Собака", нужно вывести на экран картинку step_6_pic;

        if (getMessageText().equals("/bye")) {
            sendTextMessageAsync("Hasta la vista, baby!");
        }

        sendTextMessageAsync("Ваше любимое животное: ",
                Map.of("кошка", "cat", "собакен", "dog"));

        if (getCallbackQueryButtonKey().equals("cat")) {
            sendPhotoMessageAsync("step_4_pic");
        }
        if (getCallbackQueryButtonKey().equals("dog")) {
            sendPhotoMessageAsync("step_6_pic");
        }

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new MyFirstTelegramBot());
    }
}