package jngvarr.ru.pto_ackye_rzhd.telegram;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

public interface TelegramFileService {
    File downloadFile(String fileId) throws TelegramApiException, IOException, TelegramApiException, IOException;
}
