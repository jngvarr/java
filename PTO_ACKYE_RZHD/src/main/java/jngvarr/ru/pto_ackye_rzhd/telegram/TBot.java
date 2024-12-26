package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.config.BotConfig;
import jngvarr.ru.pto_ackye_rzhd.entities.User;
import jngvarr.ru.pto_ackye_rzhd.services.UserServiceImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.HELP;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.MAIN_MENU;


@Data
@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
public class TBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UserServiceImpl service;
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";
    private List<Message> sendMessages = new ArrayList<>();

    public TBot(BotConfig config, UserServiceImpl service) {
        super( config.getBotToken());
        this.config = config;
        this.service = service;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot`s command list" + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update);
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private <T> void executeRequest(T method) {
        try {
            execute(method);
        } catch (TelegramApiException e) {
            log.error("Telegram API error: {}", e.getMessage());
        }
    }


    private void handleTextMessage(Update update) {
        String msgText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        // Упрощена логика с использованием switch
        switch (msgText) {
            case "/start":
                handleStartCommand(chatId, update.getMessage().getChat().getFirstName(), update);
                break;
            case "/help":
                sendMessage(chatId, PtoTelegramBotContent.HELP, null);
                break;
            case "/register":
                registerUser(chatId);
                break;
            default:
                sendMessage(chatId, "Sorry, the command was not recognized!", null);
        }
    }
    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        String responseText = switch (callbackData) {
            case YES_BUTTON -> "You pressed YES button";
            case NO_BUTTON -> "You pressed NO button";
            default -> null;
        };

        if (responseText != null) {
            editMessageText(responseText, chatId, messageId);
        }
    }

    private void editMessageText(String text, long chatId, int messageId) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(text);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("{} {}", ERROR_TEXT, e.getMessage());
        }
    }

    private void handleStartCommand(long chatId, String firstName, Update update) {
        String welcomeMessage = String.format("Hi, %s, nice to meet you! What do you want to do?", firstName);
        log.info("Replied to user: {}", firstName);

        sendMessage(chatId, welcomeMessage, null);
        sendTextMessageAsync(PtoTelegramBotContent.MAIN_MENU, Map.of(
                "ПТО", "pto",
                "ОТО", "oto",
                "Монтаж новой ТУ", "newTU"
        ), update);
    }

    private void registerUser(long chatId) {
        InlineKeyboardMarkup keyboardMarkup = createInlineKeyboardMarkup(Map.of(
                "Yes", YES_BUTTON,
                "No", NO_BUTTON
        ));

        sendMessage(chatId, "Do you really want to register?", keyboardMarkup);
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(Map<String, String> buttons) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        buttons.forEach((label, callbackData) -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(label);
            button.setCallbackData(callbackData);
            keyboard.add(List.of(button));
        });

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    private void sendMessage(long chatId, String textToSend, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        if (replyKeyboard instanceof ReplyKeyboardMarkup) {
            message.setReplyMarkup(replyKeyboard);
        } else if (replyKeyboard instanceof InlineKeyboardMarkup) {
            message.setReplyMarkup(replyKeyboard);
        }

        executeMessage(message);
    }



    private void executeMessage(SendMessage message) {
        try {
            execute(message); // Отправляем сообщение в Telegram
        } catch (
                TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

//    public void handleUpdate(Update update) {
//        if (service.getRepository().findById(update.getMessage().getChatId()).isEmpty()) {
//
//            User user = service.createUser(update);
//            service.registerUser(user);
//            log.info("user saved: " + user);
//        }
//    }

//    private void startCommandReceived(long chatId, String name) {
//        String answer = "Hi, " + name + ", nice to meet you!\n" +
//                "What do you want to do?";
//        log.info("Replied to user " + name);
//        sendMessage(chatId, answer, replyKeyboardMarkup());
//        sendTextMessageAsync(MAIN_MENU,
//                Map.of("ПТО", "pto", "ОТО", "oto", "Монтаж новой ТУ", "newTU"), update);
//    }

//    private ReplyKeyboardMarkup replyKeyboardMarkup() {
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
////        replyKeyboardMarkup.setResizeKeyboard(true); // Автоматически изменяет размер кнопок
//        replyKeyboardMarkup.setOneTimeKeyboard(true); // Клавиатура будет отображаться постоянно
////        replyKeyboardMarkup.setSelective(true); // Отображение только для пользователей, взаимодействующих с ботом
//
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();
//        row.add("start");
//        row.add("help");
//        keyboardRows.add(row);
//        replyKeyboardMarkup.setKeyboard(keyboardRows);
//        return replyKeyboardMarkup;
//    }

    public void sendTextMessageAsync(String text, Map<String, String> buttons, Update update) {
        try {
            SendMessage message = createMessage(text, buttons, update);
            var task = sendApiMethodAsync(message);
            this.sendMessages.add(task.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SendMessage createMessage(String text, Map<String, String> buttons, Update update) {
        SendMessage message = createMessage(text, update);
        if (buttons != null && !buttons.isEmpty())
            attachButtons(message, buttons);
        return message;
    }

    public SendMessage createMessage(String text, Update update) {
        SendMessage message = new SendMessage();
        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        Long chatId = getCurrentChatId(update);
        message.setChatId(chatId);
        return message;
    }

    private void attachButtons(SendMessage message, Map<String, String> buttons) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (String buttonName : buttons.keySet()) {
            String buttonValue = buttons.get(buttonName);

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(new String(buttonName.getBytes(), StandardCharsets.UTF_8));
            button.setCallbackData(buttonValue);

            keyboard.add(List.of(button));
        }

        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);
    }

    public Long getCurrentChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }

        return null;
    }

    private void register(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Do you really want to register?");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);
        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);

        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

//    @Override
//    public String getBotUsername() {
//        return null;
//    }
//
//    @Override
//    public void onRegister() {
//        super.onRegister();
//    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
