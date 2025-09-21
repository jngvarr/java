package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.telegram.config.BotConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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


@Data
@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
public class TBot extends TelegramLongPollingBot /*implements BotExecutor*/ {

    private final BotConfig config;
    private final ApplicationEventPublisher publisher;

    public TBot(BotConfig config, ApplicationEventPublisher publisher) {
        super(config.getBotToken());
        this.config = config;
        this.publisher = publisher;

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
    }

    @Override
    public void onUpdateReceived(Update update) {
        publisher.publishEvent(new UpdateEvent(update));;
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
//
//    @Override
//    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(Method method) {
//        try {
//            return super.executeAsync(method);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException("Ошибка executeAsync", e);
//        }
//    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}
