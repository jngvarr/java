package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.telegram.config.BotConfig;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.User;
import jngvarr.ru.pto_ackye_rzhd.telegram.handlers.CallbackQueryHandler;
import jngvarr.ru.pto_ackye_rzhd.telegram.handlers.PhotoMessageHandler;
import jngvarr.ru.pto_ackye_rzhd.telegram.handlers.TextMessageHandler;
import jngvarr.ru.pto_ackye_rzhd.domain.services.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;


@Data
@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
public class TBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final TBotMessageService messageService;
    private final PhotoMessageHandler photoMessageHandler;
    private final TextMessageHandler textMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private UserServiceImpl userService;
    private Map<Long, Integer> sentMessagesIds = new HashMap<>();
    private List<Message> sentMessages = new ArrayList<>();
    private static final long ADMIN_CHAT_ID = 199867696L;
    private boolean isPTO;
    private int sequenceNumber = 0;
    private String processInfo = "";


    public TBot(BotConfig config, TBotMessageService messageService, PhotoMessageHandler photoMessageHandler, TextMessageHandler textMessageHandler, CallbackQueryHandler callbackQueryHandler) throws TelegramApiException {
        super(config.getBotToken());
        this.config = config;
        this.messageService = messageService;
        this.photoMessageHandler = photoMessageHandler;
        this.textMessageHandler = textMessageHandler;
        this.callbackQueryHandler = callbackQueryHandler;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать работу"));
        listOfCommands.add(new BotCommand("/help", "Немного информации по использованию бота"));
        listOfCommands.add(new BotCommand("/stop", "Сбросить всё, начать заново"));
        listOfCommands.add(new BotCommand("/register", "Регистрация нового пользователя"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot`s command list" + e.getMessage());
        }
        execute(new SendMessage("199867696", INTRO));
    }

    @Override
    public void onUpdateReceived(Update update) {
        long userId;
        long chatId;
        if (update.hasMessage()) {
            forwardMessage(update.getMessage());
            userId = update.getMessage().getFrom().getId();
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
        } else {
            return;
        }
        User user = userService.getUserById(userId);
        String incomingText = update.hasMessage() && update.getMessage().hasText()
                ? update.getMessage().getText()
                : "";

        if (user == null && "/register".equals(incomingText)) {
            userService.registerUser(update);
            sendMessage(chatId, userId, "Пользователь успешно зарегистрирован.");
            return;
        } else if ("/register".equals(incomingText)) {
            sendMessage(chatId, userId, "Вы уже зарегистрированы!!!");
        }

        if (user == null || !user.isAccepted()) {
            sendMessage(chatId, userId, "Пожалуйста, пройдите регистрацию и дождитесь валидации администратора.");
            return;
        }

        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                textMessageHandler.handleTextMessage(update);
            } else if (update.getMessage().hasPhoto()) {
                photoMessageHandler.handlePhotoMessage(update);
            }
        } else if (update.hasCallbackQuery()) {
            callbackQueryHandler.handleCallbackQuery(update);
        }
    }

    void forwardMessage(Message userMessage) {
        ForwardMessage forward = new ForwardMessage();
        forward.setChatId(String.valueOf(ADMIN_CHAT_ID)); // куда
        forward.setFromChatId(String.valueOf(userMessage.getChatId())); // откуда
        forward.setMessageId(userMessage.getMessageId()); // какое сообщение

        try {
            execute(forward);
        } catch (TelegramApiException e) {
            log.error("Не удалось переслать сообщение админу: " + e.getMessage());
        }
    }

    public void sendMessage(long chatId, long userId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message, chatId);
    }

    public void editMessage(long chatId, long userId, String newTextToReplace) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(sentMessagesIds.get(userId));
        editMessage.setText(newTextToReplace);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message, long userId) {
        try {
            Message sentMessage = execute(message); // Отправляем сообщение в Telegram
            sentMessagesIds.put(userId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    public void sendTextMessage(String text, Map<String, String> buttons, Long chatId, long userId, int columns) {
        try {
            SendMessage message = createMessage(text, buttons, chatId, columns);
            var task = sendApiMethodAsync(message);
            Message sentMessage = task.get();
            this.sentMessages.add(sentMessage);
            sentMessagesIds.put(userId, sentMessage.getMessageId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void editTextAndButtons(String text, Map<String, String> buttons, Long chatId, Long userId, int columns) {
        Integer messageId = sentMessagesIds.get(userId);
        if (messageId == null) {
            log.warn("Нет messageId для userId {}", userId);
            return;
        }

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        editMessage.setParseMode("markdown");

        // Создаём временный объект для формирования разметки
        SendMessage temp = new SendMessage();
        attachButtons(temp, buttons, columns);
        editMessage.setReplyMarkup((InlineKeyboardMarkup) temp.getReplyMarkup());

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }


    public SendMessage createMessage(String text, Map<String, String> buttons, Long userId, int columns) {
        SendMessage message = createMessage(text, userId);
        if (buttons != null && !buttons.isEmpty())
            attachButtons(message, buttons, columns);
        return message;
    }


    public SendMessage createMessage(String text, Long chatId) {
        SendMessage message = new SendMessage();
        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        message.setChatId(chatId);
        return message;
    }


    private void attachButtons(SendMessage message, Map<String, String> buttons, int columns) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        Iterator<Map.Entry<String, String>> iterator = buttons.entrySet().iterator();
        while (iterator.hasNext()) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            // Добавляем до columns кнопок в текущий ряд
            for (int i = 0; i < columns && iterator.hasNext(); i++) {
                Map.Entry<String, String> entry = iterator.next();
                String buttonName = entry.getKey();
                String buttonValue = entry.getValue();

                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(new String(buttonName.getBytes(), StandardCharsets.UTF_8));
                button.setCallbackData(buttonValue);
                row.add(button); // Добавляем кнопку в ряд
            }
            keyboard.add(row);
        }
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);
    }

//    private void prepareMountedDeviceRow(Row newOtoRow, String logData) {
//        String[] dataParts = logData.split("_");
//
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
