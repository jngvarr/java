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
    private final PhotoMessageHandler photoMessageHandler;
    private final TextMessageHandler textMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final TBotMessageService tBotMessageService;
    private UserServiceImpl userService;

    public TBot(BotConfig config, PhotoMessageHandler photoMessageHandler, TextMessageHandler textMessageHandler, CallbackQueryHandler callbackQueryHandler, TBotMessageService tBotMessageService) throws TelegramApiException {
        super(config.getBotToken());
        this.config = config;
        this.photoMessageHandler = photoMessageHandler;
        this.textMessageHandler = textMessageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.tBotMessageService = tBotMessageService;

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
            tBotMessageService.forwardMessage(update.getMessage());
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
            tBotMessageService.sendMessage(chatId, userId, "Пользователь успешно зарегистрирован.");
            return;
        } else if ("/register".equals(incomingText)) {
            tBotMessageService.sendMessage(chatId, userId, "Вы уже зарегистрированы!!!");
        }

        if (user == null || !user.isAccepted()) {
            tBotMessageService.sendMessage(chatId, userId, "Пожалуйста, пройдите регистрацию и дождитесь валидации администратора.");
            return;
        }

        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                textMessageHandler.handleTextMessage(update);
            } else if (update.getMessage().hasPhoto()) {
                photoMessageHandler.handlePhotoMessage(update);
            }
        } else if (update.hasCallbackQuery()) {
            callbackQueryHandler.handleCallbackQuery(update, user);
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
