package jngvarr.ru.pto_ackye_rzhd.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class TelegramFileServiceImpl implements TelegramFileService {

    private final BotExecutor tBot;

    @Override
    public File downloadFile(String fileId) throws TelegramApiException, IOException {
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(fileId);
        org.telegram.telegrambots.meta.api.objects.File telegramFile = tBot.execute(getFileMethod);

        String filePath = telegramFile.getFilePath();
        String fileUrl = "https://api.telegram.org/file/bot" + tBot.getConfig().getBotToken() + "/" + filePath;

        Path tempFile = Files.createTempFile("photo_", ".jpg");
        try (InputStream in = new URL(fileUrl).openStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile.toFile();
    }
}
