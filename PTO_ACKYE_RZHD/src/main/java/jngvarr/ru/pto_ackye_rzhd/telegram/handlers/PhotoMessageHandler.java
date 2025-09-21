package jngvarr.ru.pto_ackye_rzhd.telegram.handlers;

import jngvarr.ru.pto_ackye_rzhd.application.services.PhotoPathService;
import jngvarr.ru.pto_ackye_rzhd.telegram.*;
import jngvarr.ru.pto_ackye_rzhd.domain.value.OtoType;
import jngvarr.ru.pto_ackye_rzhd.domain.value.PendingPhoto;
import jngvarr.ru.pto_ackye_rzhd.domain.value.PhotoState;
import jngvarr.ru.pto_ackye_rzhd.domain.value.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.application.services.PreparingPhotoService;
import jngvarr.ru.pto_ackye_rzhd.application.services.TBotConversationStateService;
import jngvarr.ru.pto_ackye_rzhd.telegram.service.TBotMessageService;
import jngvarr.ru.pto_ackye_rzhd.telegram.service.TelegramFileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class PhotoMessageHandler implements UpdateHandler {

    private final TBotMessageService tBotMessageService;
    private final TelegramFileService telegramFileService;
    // Карта для хранения информации о фото, ожидающих подтверждения
    private final PreparingPhotoService preparingPhotoService;
    private final TBotConversationStateService conversationStateService;
    private final PhotoPathService photoPathService;

    @Override
    public void handle(Update update) {
        long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();
        String processInfo = conversationStateService.getProcessInfo(userId);

        // Проверяем, есть ли подпись к фото
        String manualInput = update.getMessage().getCaption();

        // Если фото не запрашивалось
        if (!conversationStateService.getProcessStates().containsKey(userId)) {
            tBotMessageService.sendMessage(chatId, userId, "Фото не запрашивалось. Если хотите начать, нажмите /start");
            return;
        }
        tBotMessageService.sendMessage(chatId, userId, "Подождите, идёт обработка фото....");
        ProcessState currentState = conversationStateService.getProcessState(userId);
        // Получаем самое большое фото
        var photos = update.getMessage().getPhoto();
        var photo = photos.get(photos.size() - 1);
        String fileId = photo.getFileId();

        try {
            // Скачивание файла с сервера Telegram
            File downloadedFile = telegramFileService.downloadFile(fileId);

            // 3. Читаем изображение
            BufferedImage bufferedImage = ImageIO.read(downloadedFile);
            if (bufferedImage == null) {
                tBotMessageService.sendMessage(chatId, userId, "Не удалось обработать изображение.");
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
            PendingPhoto pendingPhoto = new PendingPhoto(type, downloadedFile.toPath(), barcodeText);
            conversationStateService.setPendingPhoto(userId, pendingPhoto);
            if (type.equals("counter")) {
                if (manualInput != null) pendingPhoto.setAdditionalInfo(manualInput.trim());

                // 7. Если штрихкод найден и есть показания – сразу сохраняем
                if (barcodeText != null && pendingPhoto.getAdditionalInfo() != null) {
                    savePhoto(userId, chatId, pendingPhoto);
                    return;
                }
                if (barcodeText == null) {
                    tBotMessageService.sendMessage(chatId, userId, "Штрихкод не найден. Введите номер ПУ вручную:");
                    conversationStateService.setProcessState(userId, ProcessState.MANUAL_INSERT_METER_NUMBER);
                    return;
                }
                if (manualInput == null) {
                    tBotMessageService.sendMessage(chatId, userId, "Показания счетчика не введены. Введите показания счётчика:");
                    conversationStateService.setProcessState(userId, ProcessState.MANUAL_INSERT_METER_INDICATION);
                }

            } else if (type.equals("tt")) {
                if (manualInput != null) {
                    pendingPhoto.setAdditionalInfo(manualInput);
                    savePhoto(userId, chatId, pendingPhoto);
                } else {
                    PhotoState photoState = conversationStateService.getPhotoState(userId);
                    OtoType otoType = conversationStateService.getOtoType(userId);
                    tBotMessageService.sendMessage(chatId, userId, "❌ Не указан номер трансформатора тока!! Повторите предыдущее действие!");
                    sendNextPhotoInstruction(userId, chatId, photoState.getNextPhotoType(otoType));
                }
            } else {
                if (manualInput != null) {
                    pendingPhoto.setDeviceNumber(manualInput.trim());
                    savePhoto(userId, chatId, pendingPhoto);
                } else {
                    pendingPhoto.setAdditionalInfo("Данные не требуются.");
//                    PhotoState photoState = conversationStateService.getPhotoStates().get(userId);
//                    OtoType otoType = conversationStateService.getOtoType(userId);
                    tBotMessageService.sendMessage(chatId, userId, "❌ Номер концентратора не обнаружен!! Пожалуйста введите еще раз:");
                    conversationStateService.setProcessState(userId, ProcessState.MANUAL_INSERT_METER_NUMBER);
//                    sendNextPhotoInstruction(userId, photoState.getNextPhotoType(otoType));
                }
            }
        } catch (Exception e) {
            log.error("Ошибка обработки фото: " + e.getMessage());
            tBotMessageService.sendMessage(chatId, userId, "Произошла ошибка при обработке фото.");
        }
    }

    private void sendNextPhotoInstruction(long userId, long chatId, String nextPhotoType) {
        if (nextPhotoType == null) return;

        String message = switch (nextPhotoType) {
            case "демонтирован" -> "📸 Пожалуйста, загрузите фото **ДЕМОНТИРОВАННОГО** прибора и введите показания.";
            case "установлен" -> "📸 Пожалуйста, загрузите фото **УСТАНОВЛЕННОГО** прибора и введите "
                    + (conversationStateService.getProcessState(userId).equals(ProcessState.IIK_WORKS) ? "показания" : "его номер");
            case "ф.A" -> "📸 Прикрепите фото **ТТ фазы A** и введите его номер:";
            case "ф.B" -> "📸 Прикрепите фото **ТТ фазы B** и введите его номер:";
            case "ф.C" -> "📸 Прикрепите фото **ТТ фазы C** и введите его номер:";
            default -> null;
        };

        if (message != null) {
            tBotMessageService.sendMessage(chatId, userId, message);
        }
    }

    /**
     * Обрабатывает фото, без дополнительных параметров сохранения
     */
    private void handleUncontrolledPhoto(long userId, long chatId, PendingPhoto pending) {
        doSave(userId, chatId, pending);
        conversationStateService.clearPendingPhoto(userId);
        tBotMessageService.editTextAndButtons("📸 Загрузите следующее фото или завершите загрузку.", COMPLETE_BUTTON, chatId, userId, 1);
    }

    /**
     * Обрабатывает фото, связанных с заменой оборудования
     */
    private void handleChangingEquipmentPhoto(long userId, long chatId, PendingPhoto pending, OtoType operationType, PhotoState photoState) {
        // Определяем, необходимость загрузки нового фото
        String photoPhase = photoState.getNextPhotoType(operationType);
        if (photoPhase == null) {
            tBotMessageService.editMessage(chatId, userId, "⚠ Ошибка: уже загружены все необходимые фото.");
            return;
        }
        // Сохранение фото
        doSave(userId, chatId, pending);
        photoState.markPhotoUploaded(photoPhase);

        addChangingInfo(pending, userId);
        conversationStateService.clearPendingPhoto(userId);

        // Проверка необходимости продолжения загрузки фото
        if (photoState.isComplete(operationType)) {
            tBotMessageService.sendMessage(chatId, userId, "✅ Все фото загружены!");
            changeReasonInput(chatId, userId, operationType);
            conversationStateService.clearPendingPhoto(userId);
        } else {
            // Рекомендации по загрузке фото
            sendNextPhotoInstruction(userId, chatId, photoState.getNextPhotoType(operationType));
            setProcessState(operationType, userId);
        }
    }

    private void addChangingInfo(PendingPhoto pending, long userId) {
        if (pending.getType().equals("counter")) {
            conversationStateService.appendProcessInfo(userId, pending.getDeviceNumber() + "_" + pending.getAdditionalInfo() + "_");
        } else
            conversationStateService.appendProcessInfo(userId, pending.getType().equals("concentrator") ? pending.getDeviceNumber() + "_" : pending.getAdditionalInfo() + "_");

    }

    public void changeReasonInput(long chatId, long userId, OtoType operationType) {
        tBotMessageService.editMessage(chatId, userId, "Введите причину замены: ");
        conversationStateService.setSequenceNumber(userId, REPLACED_EQUIPMENT_DATUM.get(operationType).size());
        conversationStateService.clearProcessState(userId);

    }

    private void setProcessState(OtoType operationType, long userId) {
        switch (operationType) {
            case METER_CHANGE -> conversationStateService.setProcessState(userId, ProcessState.WAITING_FOR_METER_PHOTO);
            case TT_CHANGE -> conversationStateService.setProcessState(userId, ProcessState.WAITING_FOR_TT_PHOTO);
            default -> {
            }
        }
    }

    public void doSave(long userId, long chatId, PendingPhoto pending) {
//        OtoType operationType = otoTypes.get(userId);
        try {
            Path userDir = Paths.get(photoPathService.createSavingPath(pending, userId, conversationStateService));

            Files.createDirectories(userDir);

            String newFileName = photoPathService.createNewFileName(pending, userId, conversationStateService);
            Path destination = userDir.resolve(newFileName);

            // Сохранение
            Files.move(pending.getTempFilePath(), destination, StandardCopyOption.REPLACE_EXISTING);
            tBotMessageService.editMessage(chatId, userId, "Фото сохранено!\nФайл: " + newFileName);
        } catch (IOException e) {
            log.error("❌ Ошибка сохранения фото для userId {}: {}", userId, e.getMessage(), e);
            tBotMessageService.sendMessage(chatId, userId, "⚠ Ошибка при сохранении фото. Попробуйте снова.");
        }
    }

    public void savePhoto(long userId, long chatId, PendingPhoto pending) {
        OtoType operationType = conversationStateService.getOtoType(userId);
        String deviceNumber = pending.getDeviceNumber();
        // Получаем состояние загрузки фото (если нет, создаем новое)
        PhotoState photoState = conversationStateService.getPhotoStates().computeIfAbsent(userId, key -> new PhotoState(deviceNumber));
        if (conversationStateService.getPtoFlag(userId) || !PHOTO_SUBDIRS_NAME.containsKey(operationType)) { // Только фото ПТО
            handleUncontrolledPhoto(userId, chatId, pending);
            return;
        }
        handleChangingEquipmentPhoto(userId, chatId, pending, operationType, photoState);
    }

    @Override
    public boolean canHandle(Update update) {
        return update.getMessage().hasPhoto();
    }
}
