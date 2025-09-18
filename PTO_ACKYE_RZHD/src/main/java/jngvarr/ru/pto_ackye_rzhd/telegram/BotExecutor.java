package jngvarr.ru.pto_ackye_rzhd.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
@Component
public interface BotExecutor {
    <T extends Serializable, M extends BotApiMethod<T>> CompletableFuture<T> executeAsync(M method);
    <T extends Serializable, M extends BotApiMethod<T>> T execute(M method) throws TelegramApiException;

    TelegramBot getConfig();
}