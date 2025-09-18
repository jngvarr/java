package jngvarr.ru.pto_ackye_rzhd.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {
    boolean canHandle(Update update);
    void handle(Update update);
}
