//package jngvarr.ru.pto_ackye_rzhd.telegram;
//
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.meta.generics.TelegramBot;
//
//import java.io.Serializable;
//import java.util.concurrent.CompletableFuture;
//
//@Component
//public class BotExecutorImpl implements BotExecutor {
//
//    private final TBot tBot;
//
//    public BotExecutorImpl(@Lazy TBot tBot) {
//        this.tBot = tBot;
//    }
//
//    @Override
//    public <T extends Serializable, M extends BotApiMethod<T>> CompletableFuture<T> executeAsync(M method) {
//        try {
//            return tBot.executeAsync(method);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public <T extends Serializable, M extends BotApiMethod<T>> T execute(M method) throws TelegramApiException {
//        return tBot.execute(method);
//    }
//
//    @Override
//    public TelegramBot getConfig() {
//        return null;
//    }
//}
