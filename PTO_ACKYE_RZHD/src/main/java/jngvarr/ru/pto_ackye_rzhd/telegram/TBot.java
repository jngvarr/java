package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.domain.services.UserService;
import jngvarr.ru.pto_ackye_rzhd.telegram.config.BotConfig;
import jngvarr.ru.pto_ackye_rzhd.telegram.handlers.CallbackQueryHandler;
import jngvarr.ru.pto_ackye_rzhd.telegram.handlers.PhotoMessageHandler;
import jngvarr.ru.pto_ackye_rzhd.telegram.handlers.TextMessageHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.INTRO;


@Data
@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
public class TBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final PhotoMessageHandler photoMessageHandler;
    private final TextMessageHandler textMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final UserService userService;

    public TBot(BotConfig config,
                PhotoMessageHandler photoMessageHandler,
                TextMessageHandler textMessageHandler,
                CallbackQueryHandler callbackQueryHandler,
                UserService userService) {
        super(config.getBotToken());
        this.config = config;
        this.photoMessageHandler = photoMessageHandler;
        this.textMessageHandler = textMessageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.userService = userService;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать работу"));
        listOfCommands.add(new BotCommand("/help", "Информация по использованию бота"));
        listOfCommands.add(new BotCommand("/stop", "Сбросить всё, начать заново"));
        listOfCommands.add(new BotCommand("/register", "Регистрация нового пользователя"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot command list", e);
        }
//        executeAsync(new org.telegram.telegrambots.meta.api.methods.send.SendMessage("199867696", INTRO));

//        // регистрация бота в TelegramBotsApi (если нужно)
//        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//        botsApi.registerBot(this);
//    }
    }

    @Override
    public void onUpdateReceived(Update update) {
        var user = userService.checkUser(update);
        if (user.isAccepted()) {
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
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

//    @Override
//    public void onRegister() {
//        super.onRegister();
//    }
//    @Override
//    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(Method method) {
//        try {
//            return super.executeAsync(method);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException("Ошибка executeAsync", e);
//        }
//    }
}
