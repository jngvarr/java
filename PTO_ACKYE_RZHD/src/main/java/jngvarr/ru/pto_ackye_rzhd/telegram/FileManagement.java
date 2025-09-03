package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.util.StringUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.PHOTO_SUBDIRS_NAME;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class FileManagement {
    private final StringUtils stringUtils;
    private final ExcelFileService excelFileService;

    private static final LocalDate TODAY = LocalDate.now();
    static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    static final DateTimeFormatter DD_MM_YY = DateTimeFormatter.ofPattern("dd.MM.yy");
    public static String formattedCurrentDate = TODAY.format(YYYY_MM_DD);
    public static String straightFormattedCurrentDate = TODAY.format(DD_MM_YYYY);

    private static final String WORKING_FOLDER = "\\" + TODAY.getYear() + "\\" + TODAY.format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru-RU"))).toUpperCase();
    public static final String PHOTO_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\ФОТО (Подтверждение работ)\\" + WORKING_FOLDER;
    public static final String PLAN_OTO_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx";
    public static final String OPERATION_LOG_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\ОЖ.xlsx";

    void doSave(long userId, long chatId, PendingPhoto pending) {
//        OtoType operationType = otoTypes.get(userId);
        try {
            Path userDir = Paths.get(stringUtils.createSavingPath(pending, userId));

            Files.createDirectories(userDir);

            String newFileName = stringUtils.createNewFileName(pending, userId);
            Path destination = userDir.resolve(newFileName);

            // Сохранение
            Files.move(pending.getTempFilePath(), destination, StandardCopyOption.REPLACE_EXISTING);
            editMessage(chatId, userId, "Фото сохранено!\nФайл: " + newFileName);
        } catch (IOException e) {
            log.error("❌ Ошибка сохранения фото для userId {}: {}", userId, e.getMessage(), e);
            sendMessage(chatId, userId, "⚠ Ошибка при сохранении фото. Попробуйте снова.");
        }
    }

    void savePhoto(long userId, long chatId, PendingPhoto pending) {
        TBot.OtoType operationType = otoTypes.get(userId);
        String deviceNumber = pending.getDeviceNumber();
        // Получаем состояние загрузки фото (если нет, создаем новое)
        PhotoState photoState = photoStates.computeIfAbsent(userId, key -> new PhotoState(deviceNumber));
        if (isPTO || !PHOTO_SUBDIRS_NAME.containsKey(operationType)) { // Только фото ПТО
            handleUncontrolledPhoto(userId, chatId, pending);
            return;
        }
        handleChangingEquipmentPhoto(userId, chatId, pending, operationType, photoState);
    }

}
