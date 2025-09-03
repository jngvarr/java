package jngvarr.ru.pto_ackye_rzhd.telegram.handlers;

import jngvarr.ru.pto_ackye_rzhd.telegram.*;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.OtoType;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.PendingPhoto;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.PhotoState;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.telegram.services.FileManagement;
import jngvarr.ru.pto_ackye_rzhd.telegram.services.PreparingPhotoService;
import jngvarr.ru.pto_ackye_rzhd.telegram.services.TBotConversationStateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.COMPLETE_BUTTON;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.REPLACED_EQUIPMENT_DATUM;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class PhotoMessageHandler {

    private final TBot tBot;
    // Карта для хранения информации о фото, ожидающих подтверждения
    private final PreparingPhotoService preparingPhotoService;
    private final FileManagement fileManagement;
    private final TBotConversationStateService conversationStateService;
//    private Map<Long, PendingPhoto> pendingPhotos = new HashMap<>();
//    private Map<Long, PhotoState> photoStates = new HashMap<>();
//    private Map<Long, ProcessState> processStates = conversationStateService.getProcessStates();
//    private Map<Long, String> processInfos = conversationStateService.getProcessInfos();


    public void handlePhotoMessage(Update update) {
        long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();
        String processInfo = conversationStateService.getProcessInfo(userId);

        // Проверяем, есть ли подпись к фото
        String manualInput = update.getMessage().getCaption();

        // Если фото не запрашивалось
        if (!conversationStateService.getProcessStates().containsKey(userId)) {
            tBot.sendMessage(chatId, userId, "Фото не запрашивалось. Если хотите начать, нажмите /start");
            return;
        }
        tBot.sendMessage(chatId, userId, "Подождите, идёт обработка фото....");
        ProcessState currentState = conversationStateService.getProcessStates().get(userId);
        // Получаем самое большое фото
        var photos = update.getMessage().getPhoto();
        var photo = photos.get(photos.size() - 1);
        String fileId = photo.getFileId();

        try {
            // Скачивание файла с сервера Telegram
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);
            org.telegram.telegrambots.meta.api.objects.File telegramFile = tBot.execute(getFileMethod);
            String filePath = telegramFile.getFilePath();
            String fileUrl = "https://api.telegram.org/file/bot" + tBot.getConfig().getBotToken() + "/" + filePath;

            // 2. Сохраняем фото в папку пользователя
            Path userDir = Paths.get("photos", String.valueOf(userId));
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);

                // Сохраняем файл во временное хранилище
            }
            Path tempFilePath = Files.createTempFile(userDir, "photo_", ".jpg");
            try (InputStream in = new URL(fileUrl).openStream()) {
                Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 3. Читаем изображение
            BufferedImage bufferedImage = ImageIO.read(tempFilePath.toFile());
            if (bufferedImage == null) {
                tBot.sendMessage(chatId, userId, "Не удалось обработать изображение.");
                return;
            }

            String barcodeText = "";
            if (currentState.equals(ProcessState.WAITING_FOR_METER_PHOTO)) {
                // 4. Декодируем штрихкод
                barcodeText = preparingPhotoService.decodeBarcode(bufferedImage);
                if (barcodeText == null) {
                    barcodeText = preparingPhotoService.decodeBarcode(preparingPhotoService.resizeImage(bufferedImage,
                            bufferedImage.getWidth() * 2, bufferedImage.getHeight() * 2));
                }
                if (barcodeText == null) {
                    barcodeText = preparingPhotoService.decodeBarcode(preparingPhotoService.convertToGrayscale(bufferedImage));
                }
            } else if (currentState.equals(ProcessState.WAITING_FOR_TT_PHOTO)) {
                barcodeText = processInfo.substring(0, processInfo.indexOf("_"));
            }

            // 5. Определяем тип фото (счётчик, тт или концентратор)
            String type = switch (currentState) {
                case WAITING_FOR_METER_PHOTO -> "counter";
                case WAITING_FOR_DC_PHOTO -> "concentrator";
                case WAITING_FOR_TT_PHOTO -> "tt";
                default -> throw new IllegalStateException("Неизвестный тип оборудования: " + currentState);
            };

            // 6. Создаём объект для хранения фото
            PendingPhoto pendingPhoto = new PendingPhoto(type, tempFilePath, barcodeText);
            conversationStateService.getPendingPhotos().put(userId, pendingPhoto);
            if (type.equals("counter")) {
                if (manualInput != null) pendingPhoto.setAdditionalInfo(manualInput.trim());

                // 7. Если штрихкод найден и есть показания – сразу сохраняем
                if (barcodeText != null && pendingPhoto.getAdditionalInfo() != null) {
                    fileManagement.savePhoto(userId, chatId, pendingPhoto);
                    return;
                }
                if (barcodeText == null) {
                    tBot.sendMessage(chatId, userId, "Штрихкод не найден. Введите номер ПУ вручную:");
                    conversationStateService.getProcessStates().put(userId, ProcessState.MANUAL_INSERT_METER_NUMBER);
                    return;
                }
                if (manualInput == null) {
                    tBot.sendMessage(chatId, userId, "Показания счетчика не введены. Введите показания счётчика:");
                    conversationStateService.getProcessStates().put(userId, ProcessState.MANUAL_INSERT_METER_INDICATION);
                }

            } else if (type.equals("tt")) {
                if (manualInput != null) {
                    pendingPhoto.setAdditionalInfo(manualInput);
                    fileManagement.savePhoto(userId, chatId, pendingPhoto);
                } else {
                    PhotoState photoState = conversationStateService.getPhotoStates().get(userId);
                    OtoType otoType = conversationStateService.getOtoType(userId);
                    tBot.sendMessage(chatId, userId, "❌ Не указан номер трансформатора тока!! Повторите предыдущее действие!");
                    sendNextPhotoInstruction(userId, chatId, photoState.getNextPhotoType(otoType));
                }
            } else {
                if (manualInput != null) {
                    pendingPhoto.setDeviceNumber(manualInput.trim());
                    fileManagement.savePhoto(userId, chatId, pendingPhoto);
                } else {
                    pendingPhoto.setAdditionalInfo("Данные не требуются.");
//                    PhotoState photoState = conversationStateService.getPhotoStates().get(userId);
//                    OtoType otoType = conversationStateService.getOtoType(userId);
                    tBot.sendMessage(chatId, userId, "❌ Номер концентратора не обнаружен!! Пожалуйста введите еще раз:");
                    conversationStateService.getProcessStates().put(userId, ProcessState.MANUAL_INSERT_METER_NUMBER);
//                    sendNextPhotoInstruction(userId, photoState.getNextPhotoType(otoType));
                }
            }
        } catch (Exception e) {
            log.error("Ошибка обработки фото: " + e.getMessage());
            tBot.sendMessage(chatId, userId, "Произошла ошибка при обработке фото.");
        }
    }

    private void sendNextPhotoInstruction(long userId, long chatId, String nextPhotoType) {
        if (nextPhotoType == null) return;

        String message = switch (nextPhotoType) {
            case "демонтирован" -> "📸 Пожалуйста, загрузите фото **ДЕМОНТИРОВАННОГО** прибора и введите показания.";
            case "установлен" -> "📸 Пожалуйста, загрузите фото **УСТАНОВЛЕННОГО** прибора и введите "
                    + (conversationStateService.getProcessStates().get(userId).equals(ProcessState.IIK_WORKS) ? "показания" : "его номер");
            case "ф.A" -> "📸 Прикрепите фото **ТТ фазы A** и введите его номер:";
            case "ф.B" -> "📸 Прикрепите фото **ТТ фазы B** и введите его номер:";
            case "ф.C" -> "📸 Прикрепите фото **ТТ фазы C** и введите его номер:";
            default -> null;
        };

        if (message != null) {
            tBot.sendMessage(chatId, userId, message);
        }
    }

    /**
     * Обрабатывает фото, без дополнительных параметров сохранения
     */
    private void handleUncontrolledPhoto(long userId, long chatId, PendingPhoto pending) {
        fileManagement.doSave(userId, chatId, pending);
        conversationStateService.getPendingPhotos().remove(userId);
        tBot.editTextAndButtons("📸 Загрузите следующее фото или завершите загрузку.", COMPLETE_BUTTON, chatId, userId, 1);
    }

    /**
     * Обрабатывает фото, связанных с заменой оборудования
     */
    private void handleChangingEquipmentPhoto(long userId, long chatId, PendingPhoto pending, OtoType operationType, PhotoState photoState) {
        // Определяем, необходимость загрузки нового фото
        String photoPhase = photoState.getNextPhotoType(operationType);
        if (photoPhase == null) {
            tBot.editMessage(chatId, userId, "⚠ Ошибка: уже загружены все необходимые фото.");
            return;
        }
        // Сохранение фото
        fileManagement.doSave(userId, chatId, pending);
        photoState.markPhotoUploaded(photoPhase);

        addChangingInfo(pending, userId);
        conversationStateService.getPendingPhotos().remove(userId);

        // Проверка необходимости продолжения загрузки фото
        if (photoState.isComplete(operationType)) {
            tBot.sendMessage(chatId, userId, "✅ Все фото загружены!");
            changeReasonInput(chatId, userId, operationType);

            conversationStateService.getPendingPhotos().remove(userId);
        } else {
            // Рекомендации по загрузке фото
            sendNextPhotoInstruction(userId, chatId, photoState.getNextPhotoType(operationType));
            setProcessState(operationType, userId);
        }
    }

    private void addChangingInfo(PendingPhoto pending, long userId) {
        if (pending.getType().equals("counter")) {
            conversationStateService.appendProcessInfo(userId,pending.getDeviceNumber() + "_" + pending.getAdditionalInfo() + "_");
        } else
            conversationStateService.appendProcessInfo(userId, pending.getType().equals("concentrator") ? pending.getDeviceNumber() + "_" : pending.getAdditionalInfo() + "_");

    }

    public void changeReasonInput(long chatId, long userId, OtoType operationType) {
        tBot.editMessage(chatId, userId, "Введите причину замены: ");
        conversationStateService.setSequenceNumber(userId, REPLACED_EQUIPMENT_DATUM.get(operationType).size());
        conversationStateService.clearProcessState(userId);

    }

    private void setProcessState(OtoType operationType, long userId) {
        switch (operationType) {
            case METER_CHANGE -> conversationStateService.getProcessStates().put(userId, ProcessState.WAITING_FOR_METER_PHOTO);
            case TT_CHANGE -> conversationStateService.getProcessStates().put(userId, ProcessState.WAITING_FOR_TT_PHOTO);
            default -> {
            }
        }
    }
}
