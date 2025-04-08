package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.config.BotConfig;
import jngvarr.ru.pto_ackye_rzhd.services.UserServiceImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static jngvarr.ru.pto_ackye_rzhd.telegram.FileManagement.*;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;


@Data
@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
public class TBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UserServiceImpl service;
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";
    private List<Message> sendMessages = new ArrayList<>();
    private final ExcelFileService excelFileService;
    private final PreparingPhotoService preparingPhotoService;
    // Карта для хранения информации о фото, ожидающих подтверждения
    private Map<Long, PendingPhoto> pendingPhotos = new HashMap<>();

    public enum UserState {
        WAITING_FOR_COUNTER_PHOTO,
        WAITING_FOR_DC_PHOTO,
        WAITING_FOR_TT_PHOTO,
        MANUAL_INSERT_METER_NUMBER,
        MANUAL_INSERT_METER_INDICATION
    }

    private void handleStartCommand(long chatId, String firstName) {
        sendTextMessage(MAIN_MENU, startMenuButtons, chatId, 1);
    }

    private Map<String, String> startMenuButtons = Map.of(
            "ПТО", "pto",
            "ОТО", "oto",
            "Монтаж / демонтаж ТУ", "newTU"
    );

    private Map<String, Map<String, String>> modes = Map.of(
            "pto", Map.of(
                    "Добавление фото счетчика", "ptoIIK",
                    "Добавление фото ИВКЭ", "ptoIVKE"),
            "oto", Map.of(
                    "ОТО ИИК", "otoIIK",
                    "ОТО ИВКЭ", "otoIVKE"),
            "newTU", Map.of(
                    "Монтаж новой точки учёта", "addIIK",
                    "Демонтаж точки учёта", "delIIK",
                    "Монтаж концентратора", "dcMount",
                    "Демонтаж концентратора", "dcRemove")
    );

    // Карта для хранения состояния диалога по chatId
    private Map<Long, UserState> userStates = new HashMap<>();
    private Map<Long, OtoType> otoTypes = new HashMap<>();
    private Map<String, String> otoLog = new HashMap<>();
    private Map<Long, PhotoState> photoStates = new HashMap<>();
    private Map<OtoType, String> PHOTO_SUBDIRS_NAME = Map.of(
            OtoType.METER_CHANGE, "Замена ПУ",
            OtoType.TT_CHANGE, "Замена ТТ"
    );

    public enum OtoType {
        WK_DROP, METER_CHANGE, SET_NOT, SUPPLY_RESTORING, TT_CHANGE, DC_CHANGE, DC_RESTART
    }

    String chgePath;
    private int sequenceNumber = 0;

    private Map<String, String> otoIIKButtons = Map.of(
            "Сброшена ошибка ключа (WK)", "wkDrop",
            "Замена счетчика", "meterChange",
            "Замена трансформаторов тока", "ttChange",
            "Восстановление питания ТУ", "powerSupplyRestoring",
            "Присвоение статуса НОТ", "setNot");

    private Map<String, String> otoIVKEButtons = Map.of(
            "Замена концентратора", "dcChange",
            "Перезагрузка концентратора", "dcRestart",
            "Восстановление питания", "powerSupplyRestoring");

    private Map<String, String> confirmMenu = Map.of(
            "Подтвердить выполнение", "confirm",
            "Отменить выполнение", "cancel");

    private Map<String, String> savingPaths = getPhotoSavingPathFromExcel();
    private Map<String, String> CompleteButton = Map.of("Завершить загрузку данных", "LOADING_COMPLETE");
    private int photoCounter;
    private String deviceChangeInfo = "";

    public TBot(BotConfig config, UserServiceImpl service, ExcelFileService excelFileService, PreparingPhotoService preparingPhotoService) {
        super(config.getBotToken());
        this.config = config;
        this.service = service;
        this.excelFileService = excelFileService;
        this.preparingPhotoService = preparingPhotoService;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot`s command list" + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            // Если сообщение содержит текст
            if (update.getMessage().hasText()) {
                handleTextMessage(update);
            }
            // Если сообщение содержит фото
            else if (update.getMessage().hasPhoto()) {
                handlePhotoMessage(update);
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private void handlePhotoMessage(Update update) {
        long chatId = update.getMessage().getChatId();

        // Проверяем, есть ли подпись к фото
        String manualInput = update.getMessage().getCaption();

        // Если фото не запрашивалось
        if (!userStates.containsKey(chatId)) {
            sendMessage(chatId, "Фото не запрашивалось. Если хотите начать, нажмите /start");
            return;
        }
        sendMessage(chatId, "Подождите, идёт обработка фото....");
        UserState currentState = userStates.get(chatId);
        // Получаем самое большое фото
        var photos = update.getMessage().getPhoto();
        var photo = photos.get(photos.size() - 1);
        String fileId = photo.getFileId();

        try {
            // Скачивание файла с сервера Telegram
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);
            org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFileMethod);
            String filePath = telegramFile.getFilePath();
            String fileUrl = "https://api.telegram.org/file/bot" + config.getBotToken() + "/" + filePath;

            // 2. Сохраняем фото в папку пользователя
            Path userDir = Paths.get("photos", String.valueOf(chatId));
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
                sendMessage(chatId, "Не удалось обработать изображение.");
                return;
            }

            String barcodeText = "";
            if (currentState.equals(UserState.WAITING_FOR_COUNTER_PHOTO)) {
                // 4. Декодируем штрихкод
                barcodeText = preparingPhotoService.decodeBarcode(bufferedImage);
                if (barcodeText == null) {
                    barcodeText = preparingPhotoService.decodeBarcode(preparingPhotoService.resizeImage(bufferedImage,
                            bufferedImage.getWidth() * 2, bufferedImage.getHeight() * 2));
                }
                if (barcodeText == null) {
                    barcodeText = preparingPhotoService.decodeBarcode(preparingPhotoService.convertToGrayscale(bufferedImage));
                }
            } else if (currentState.equals(UserState.WAITING_FOR_TT_PHOTO)) {
                barcodeText = deviceChangeInfo.substring(0, deviceChangeInfo.indexOf("_"));
            }

            // 5. Определяем тип фото (счётчик, тт или концентратор)
            String type = switch (currentState) {
                case WAITING_FOR_COUNTER_PHOTO -> "counter";
                case WAITING_FOR_DC_PHOTO -> "concentrator";
                case WAITING_FOR_TT_PHOTO -> "tt";
                default -> throw new IllegalStateException("Неизвестный тип оборудования: " + currentState);
            };

            // 6. Создаём объект для хранения фото
            PendingPhoto pendingPhoto = new PendingPhoto(type, tempFilePath, barcodeText);
            pendingPhotos.put(chatId, pendingPhoto);
            if (type.equals("counter")) {
                if (manualInput != null) pendingPhoto.setAdditionalInfo(manualInput.trim());

                // 7. Если штрихкод найден и есть показания – сразу сохраняем
                if (barcodeText != null && pendingPhoto.getAdditionalInfo() != null) {
                    savePhoto(chatId, pendingPhoto);
                    return;
                }
                if (barcodeText == null) {
                    sendMessage(chatId, "Штрихкод не найден. Введите номер ПУ вручную:");
                    userStates.put(chatId, UserState.MANUAL_INSERT_METER_NUMBER);
                    return;
                }
                if (manualInput == null) {
                    sendMessage(chatId, "Показания счетчика не введены. Введите показания счётчика:");
                    userStates.put(chatId, UserState.MANUAL_INSERT_METER_INDICATION);
                }

            } else if (type.equals("tt")) {
                if (manualInput != null) {
                    pendingPhoto.setAdditionalInfo(manualInput);
                    savePhoto(chatId, pendingPhoto);
                } else {
                    PhotoState photoState = photoStates.get(chatId);
                    OtoType otoType = otoTypes.get(chatId);
                    sendMessage(chatId, "❌ Не указан номер трансформатора тока!! Повторите предыдущее действие!");
                    sendNextPhotoInstruction(chatId, photoState.getNextPhotoType(otoType));
                }
            }
        } catch (Exception e) {
            log.error("Ошибка обработки фото: " + e.getMessage());
            sendMessage(chatId, "Произошла ошибка при обработке фото.");
        }
    }


    private void handleTextMessage(Update update) {
        String msgText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        UserState userState = userStates.get(chatId);
        OtoType otoType = otoTypes.get(chatId);

        if (userState != null) {
            switch (userState) {
                case MANUAL_INSERT_METER_NUMBER, MANUAL_INSERT_METER_INDICATION -> {
                    handleManualInsert(chatId, msgText);
                    return;
                }
            }
        }
        if (otoType != null) {
            switch (otoType) {
                case TT_CHANGE, METER_CHANGE, DC_CHANGE -> {
                    handleEquipmentChange(chatId, msgText, otoType);
                    return;
                }
                case WK_DROP, SET_NOT, SUPPLY_RESTORING -> {
                    handleOtherOtoIIKTypes(chatId, msgText);
                    return;
                }
            }
        }

        // Обработка остальных текстовых сообщений
        switch (msgText) {
            case "/start" -> handleStartCommand(chatId, update.getMessage().getChat().getFirstName());
            case "/help" -> sendMessage(chatId, HELP);
            case "/register" -> registerUser(chatId);
            default -> sendMessage(chatId, "Команда не распознана. Попробуйте еще раз.");
        }
    }

    private void handleOtherOtoIIKTypes(long chatId, String msgText) {
        OtoType currentOtoType = otoTypes.get(chatId);
        String deviceNumber = msgText.trim();
        Map<OtoType, String> otoStringMap = Map.of(
                OtoType.WK_DROP, "WK_",
                OtoType.SET_NOT, "NOT_",
                OtoType.SUPPLY_RESTORING, "SUPPLY_");

        if (deviceNumber.contains("_")) {
            String[] deviceData = deviceNumber.split("_");
            deviceNumber = deviceData[0];
            otoLog.put(deviceNumber, otoStringMap.get(currentOtoType) + deviceData[1]);
        } else {
            otoLog.put(deviceNumber, otoStringMap.get(currentOtoType));
        }
        sendTextMessage("Введите номер следующего ПУ или закончите ввод.", CompleteButton, chatId, 1);
    }


    private void handleEquipmentChange(long chatId, String msgText, OtoType otoType) {
        Map<Integer, String> replacedEquipmentData = getReplacedEquipmentData().get(otoType);
        if (msgText != null && !msgText.trim().isEmpty()) {
            deviceChangeInfo += msgText + "_";
        }
        if (sequenceNumber < replacedEquipmentData.size()) {
            sendMessage(chatId, replacedEquipmentData.get(sequenceNumber));
            sequenceNumber++;
        } else concludeDeviceChange(chatId, otoType);
    }

    Map<OtoType, Map<Integer, String>> replacedEquipmentData = Map.of(
            OtoType.TT_CHANGE, Map.of(
                    0, "Введите тип трансформаторов тока (пример: ТШП-0,66): ",
                    1, "Введите коэффициент трансформации (пример: 300/5): ",
                    2, "Введите класс точности (пример: 0,5 или 0,5S): ",
                    3, "Введите год выпуска трансформаторов (пример: 2025): ",
                    4, "Введите номер ТТ ф.A: ",
                    5, "Введите номер ТТ ф.B: ",
                    6, "Введите номер ТТ ф.C: ",
                    7, "Опишите причину замены: "),
            OtoType.METER_CHANGE, Map.of(
                    0, "Введите показания демонтируемого прибора учета:",
                    1, "Введите номер устанавливаемого прибора учета:",
                    2, "Введите показания устанавливаемого прибора учета:",
                    3, "Опишите причину замены: "),
            OtoType.DC_CHANGE, Map.of(
                    0, "Введите номер устанавливаемого концентратора.",
                    1, "Опишите причину замены: ")
    );

    private void handleManualInsert(long chatId, String deviceNumber) {
        String manualInput = deviceNumber.trim();
        PendingPhoto pending = pendingPhotos.get(chatId);
        if (pending != null) {
            boolean isDataFull = pending.getDeviceNumber() != null && pending.getAdditionalInfo() != null;
            if (userStates.get(chatId).equals(UserState.MANUAL_INSERT_METER_NUMBER)) {
                pending.setAdditionalInfo(manualInput);
            } else {
                pending.setDeviceNumber(manualInput);
            }
            if (isDataFull) savePhoto(chatId, pending);
            else if (pending.getDeviceNumber() == null) {
                sendMessage(chatId, "Штрихкод не найден. Введите номер ПУ вручную:");
                userStates.put(chatId, UserState.MANUAL_INSERT_METER_NUMBER);
            } else {
                sendMessage(chatId, "Показания счетчика не введены. Введите показания счётчика:");
                userStates.put(chatId, UserState.MANUAL_INSERT_METER_INDICATION);
            }
        } else {
            sendMessage(chatId, "Ошибка: нет ожидающих фото для привязки показаний.");
        }
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (callbackData) {
            case "newTU" -> {
                sendTextMessage(NEW_TU, modes.get(callbackData), chatId, 1);
            }
            case "pto" -> {
                sendTextMessage(PTO, modes.get(callbackData), chatId, 2);
            }
            case "oto" -> {
                sendTextMessage(OTO, modes.get(callbackData), chatId, 2);
            }

            // Обработка выбора для ПТО счетчика и концентратора
            case "ptoIIK", "ptoIVKE" -> {
                String textToSend;
                if ("ptoIIK".equals(callbackData)) {
                    textToSend = "Пожалуйста, загрузите фото счетчика.";
                    userStates.put(chatId, UserState.WAITING_FOR_COUNTER_PHOTO);
                } else {
                    textToSend = "Пожалуйста, загрузите фото концентратора.";
                    userStates.put(chatId, UserState.WAITING_FOR_DC_PHOTO);
                }
                sendMessage(chatId, textToSend);
            }

            case "otoIIK", "otoIVKE" -> {
                if (callbackData.equals("otoIIK")) {
                    sendTextMessage("Выберите вид ОТО ИИК: ", otoIIKButtons, chatId, 2);
                } else {
                    sendTextMessage("Выберите вид ОТО ИВКЭ: ", otoIVKEButtons, chatId, 2);
                }
            }


            case "wkDrop", "setNot", "powerSupplyRestoring" -> {
                switch (callbackData) {
                    case "wkDrop" -> {
                        sendMessage(chatId, "Введите номер прибора учета: ");
                        otoTypes.put(chatId, OtoType.WK_DROP);
                    }
                    case "setNot" -> {
                        sendMessage(chatId, "Введите номер прибора учета: ");
                        otoTypes.put(chatId, OtoType.SET_NOT);
                    }
                    default -> {
                        sendMessage(chatId, "Введите номер прибора учета: ");
                        otoTypes.put(chatId, OtoType.SUPPLY_RESTORING);
                    }
                }
            }


            case "meterChange", "ttChange", "dcChange" -> {
                String value1 = "";
                String value2 = "";
                if (callbackData.equals("meterChange")) {
                    value1 = "meterChangeWithPhoto";
                    value2 = "meterChangeWithoutPhoto";
                } else if ("ttChange".equals(callbackData)) {
                    value1 = "ttChangeWithPhoto";
                    value2 = "ttChangeWithOutPhoto";
                } else {
                    value1 = "dcChangeWithPhoto";
                    value2 = "dcChangeWithOutPhoto";
                }
                sendTextMessage("Вид передачи данных: ",
                        Map.of("С приложением фото.", value1,
                                "Без приложения фото.", value2), chatId, 2);
            }

            case "ttChangeWithPhoto", "ttChangeWithOutPhoto" -> {
                if ("ttChangeWithPhoto".equals(callbackData))
                    userStates.put(chatId, UserState.WAITING_FOR_TT_PHOTO);
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoTypes.put(chatId, OtoType.TT_CHANGE);
            }

            case "dcChangeWithPhoto", "dcChangeWithOutPhoto" -> {
                if ("dcChangeWithOutPhoto".equals(callbackData))
                    userStates.put(chatId, UserState.WAITING_FOR_DC_PHOTO);
                sendMessage(chatId, "Введите номер демонтируемого концентратора: ");
                otoTypes.put(chatId, OtoType.DC_CHANGE);
            }

            case "meterChangeWithPhoto", "meterChangeWithoutPhoto" -> {
                String textToSend = "";
                otoTypes.put(chatId, OtoType.METER_CHANGE);

                if ("meterChangeWithPhoto".equals(callbackData)) {
                    textToSend = "📸 Пожалуйста, загрузите фото **ДЕМОНТИРОВАННОГО** прибора и введите показания.";
                    userStates.put(chatId, UserState.WAITING_FOR_COUNTER_PHOTO);
                } else textToSend = "Введите номер демонтируемого прибора учета: ";
                sendMessage(chatId, textToSend);
            }

            case "LOADING_COMPLETE" -> {
                sendTextMessage(actionConfirmation(), confirmMenu, chatId, 2);
            }

            case "confirm", "cancel" -> {
                String textToSend;
                if ("confirm".equals(callbackData)) {
                    textToSend = "Информация сохранена.";
                    sendMessage(chatId, "Подождите, идёт загрузка данных...");
                    operationLogFilling();
                } else {
                    textToSend = "Информация не сохранена.";
                }
                sendMessage(chatId, textToSend);
                clearData();
                sendMessage(chatId, "Для продолжения снова нажмите /start");
            }
            default -> sendMessage(chatId, "Неизвестное действие. Попробуйте еще раз.");
        }
    }

    private void concludeDeviceChange(long chatId, OtoType changeType) {
        formingOtoLogWithDeviceChange(deviceChangeInfo, changeType);
        sendTextMessage(actionConfirmation(), confirmMenu, chatId, 2);
    }

    private void clearData() {
        otoLog.clear();
        sequenceNumber = 0;
        userStates.clear();
        otoTypes.clear();
        deviceChangeInfo = "";
    }

    private void savePhoto(long chatId, PendingPhoto pending) {
        OtoType operationType = otoTypes.get(chatId);
        String deviceNumber = pending.getDeviceNumber();

        if (!PHOTO_SUBDIRS_NAME.containsKey(operationType)) {
            handleUncontrolledPhoto(chatId, pending);
            return;
        }

        // Получаем состояние загрузки фото (если нет, создаем новое)
        PhotoState photoState = photoStates.computeIfAbsent(chatId, key -> new PhotoState(deviceNumber));

        handleChangingEquipmentPhoto(chatId, pending, operationType, deviceNumber, photoState);
    }

    /**
     * Обрабатывает фото, без дополнительных параметров сохранения
     */
    private void handleUncontrolledPhoto(long chatId, PendingPhoto pending) {
        doSave(chatId, pending);
        pendingPhotos.remove(chatId);
        sendTextMessage("📸 Загрузите следующее фото или завершите загрузку.", CompleteButton, chatId, 1);
    }

    /**
     * Обрабатывает фото, связанных с заменой оборудования
     */
    private void handleChangingEquipmentPhoto(long chatId, PendingPhoto pending, OtoType operationType, String
            deviceNumber, PhotoState photoState) {
        // Определяем, необходимость загрузки нового фото
        String photoPhase = photoState.getNextPhotoType(operationType);
        if (photoPhase == null) {
            sendMessage(chatId, "⚠ Ошибка: уже загружены все необходимые фото.");
            return;
        }
        // Сохранение фото
        doSave(chatId, pending);
        photoState.markPhotoUploaded(photoPhase);

        editChangingInfo(pending);
        pendingPhotos.remove(chatId);

        // Проверка необходимости продолжения загрузки фото
        if (photoState.isComplete(operationType)) {
            sendMessage(chatId, "✅ Все фото загружены!");
            concludeDeviceChange(chatId, operationType);

            photoStates.remove(chatId);
        } else {
            // Рекомендации по загрузке фото
            sendNextPhotoInstruction(chatId, photoState.getNextPhotoType(operationType));
            setUserState(operationType, chatId);
        }
    }

    private void setUserState(OtoType operationType, long chatId) {
        switch (operationType) {
            case METER_CHANGE -> userStates.put(chatId, UserState.WAITING_FOR_COUNTER_PHOTO);
            case TT_CHANGE -> userStates.put(chatId, UserState.WAITING_FOR_TT_PHOTO);
            default -> {
            }
        }
        ;
    }


    private void doSave(long chatId, PendingPhoto pending) {
        OtoType operationType = otoTypes.get(chatId);
        try {
            Path userDir = Paths.get(createSavingPath(operationType, pending));

            Files.createDirectories(userDir);

            String newFileName = createNewFileName(pending, operationType);
            Path destination = userDir.resolve(newFileName);

            // Сохранение
            Files.move(pending.getTempFilePath(), destination, StandardCopyOption.REPLACE_EXISTING);
            sendMessage(chatId, "Фото сохранено!\nФайл: " + newFileName);
        } catch (IOException e) {
            log.error("❌ Ошибка сохранения фото для chatId {}: {}", chatId, e.getMessage(), e);
            sendMessage(chatId, "⚠ Ошибка при сохранении фото. Попробуйте снова.");
        }
    }

    private void sendNextPhotoInstruction(long chatId, String nextPhotoType) {
        if (nextPhotoType == null) return;

        String message = switch (nextPhotoType) {
            case "демонтирован" -> "📸 Пожалуйста, загрузите фото **ДЕМОНТИРОВАННОГО** прибора и введите показания.";
            case "установлен" -> "📸 Пожалуйста, загрузите фото **УСТАНОВЛЕННОГО** прибора и введите показания.";
            case "Фаза_A" -> "📸 Прикрепите фото **ТТ фазы A** и введите его номер:";
            case "Фаза_B" -> "📸 Прикрепите фото **ТТ фазы B** и введите его номер:";
            case "Фаза_C" -> "📸 Прикрепите фото **ТТ фазы C** и введите его номер:";
            default -> null;
        };

        if (message != null) {
            sendMessage(chatId, message);
        }
    }

    private String createSavingPath(OtoType operationType, PendingPhoto pending) {
        String baseDir = PHOTO_PATH + File.separator;

        if (operationType != null && PHOTO_SUBDIRS_NAME.containsKey(operationType)) {
            baseDir += PHOTO_SUBDIRS_NAME.get(operationType) + File.separator;
        }
        String path = savingPaths.getOrDefault(pending.getDeviceNumber(), "unknown");
        String resultPath = !PHOTO_SUBDIRS_NAME.containsKey(operationType) ? path.substring(0, path.lastIndexOf("\\")) : path;

        if (photoCounter == 1) chgePath = resultPath;//TODO заменить на PhotoState
        if (photoCounter == 2) resultPath = chgePath;
        return baseDir + resultPath;
    }

    private String createNewFileName(PendingPhoto pending, OtoType operationType) {
        String additionalInfo = pending.getAdditionalInfo();
        String deviceNumber = (pending.getDeviceNumber() != null) ? pending.getDeviceNumber() : "";
        String meterIndicationOrTtNumber = (additionalInfo != null) ? switch (operationType) {
            case METER_CHANGE -> "_" + additionalInfo;
            case TT_CHANGE -> "_(" + additionalInfo + ")";
            default -> "unknown";
        } : "";

        return formattedCurrentDate + "_" + getSavingPhotoPrefix(pending.getType()) + deviceNumber +
                meterIndicationOrTtNumber + getSavingPhotoSuffix(operationType) + ".jpg";
    }

    private String getSavingPhotoSuffix(OtoType operationType) {
        if (operationType != null && operationType.equals(OtoType.METER_CHANGE)) return switch (photoCounter) {
            case 1 -> "_демонтирован.jpg";
            case 2 -> "_установлен.jpg";
            default -> "";
        };
        return "";
    }

    private String getSavingPhotoPrefix(String type) {
        return switch (type) {
            case "counter" -> "ИИК_";
            case "concentrator" -> "ИВКЭ_";
            case "tt" -> "ТТ_";
            default -> "unknown_";
        };
    }

    private void editChangingInfo(PendingPhoto pending) {
        if (pending.getType().equals("counter")) {
            deviceChangeInfo += pending.getDeviceNumber() + "_" + pending.getAdditionalInfo() + "_";
        } else deviceChangeInfo += pending.getAdditionalInfo() + "_";
    }

    private void registerUser(long chatId) {
        sendTextMessage("Do you really want to register?",
                Map.of("Yes", YES_BUTTON, "No", NO_BUTTON), chatId, 2);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message); // Отправляем сообщение в Telegram
        } catch (
                TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    public void sendTextMessage(String text, Map<String, String> buttons, Long chatId, int columns) {
        try {
            SendMessage message = createMessage(text, buttons, chatId, columns);
            var task = sendApiMethodAsync(message);
            this.sendMessages.add(task.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SendMessage createMessage(String text, Map<String, String> buttons, Long chatId, int columns) {
        SendMessage message = createMessage(text, chatId);
        if (buttons != null && !buttons.isEmpty())
            attachButtons(message, buttons, columns);
        return message;
    }


    public SendMessage createMessage(String text, Long chatId) {
        SendMessage message = new SendMessage();
        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        message.setChatId(chatId);
        return message;
    }

    private void attachButtons(SendMessage message, Map<String, String> buttons) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (String buttonName : buttons.keySet()) {
            String buttonValue = buttons.get(buttonName);

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(new String(buttonName.getBytes(), StandardCharsets.UTF_8));
            button.setCallbackData(buttonValue);

            keyboard.add(List.of(button));
        }

        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);
    }

    private void attachButtons(SendMessage message, Map<String, String> buttons, int columns) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        Iterator<Map.Entry<String, String>> iterator = buttons.entrySet().iterator();
        while (iterator.hasNext()) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            // Добавляем до columns кнопок в текущий ряд
            for (int i = 0; i < columns && iterator.hasNext(); i++) {
                Map.Entry<String, String> entry = iterator.next();
                String buttonName = entry.getKey();
                String buttonValue = entry.getValue();

                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(new String(buttonName.getBytes(), StandardCharsets.UTF_8));
                button.setCallbackData(buttonValue);
                row.add(button); // Добавляем кнопку в ряд
            }
            keyboard.add(row);
        }
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);
    }

    public Long getCurrentChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }

        return null;
    }

    private void operationLogFilling() {
        boolean isDcChange = otoLog.values().stream().anyMatch(value -> value.contains("dcChange"));
        if (otoLog.isEmpty()) return;
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             Workbook operationLog = new XSSFWorkbook(new FileInputStream(OPERATION_LOG_PATH));
             FileOutputStream fileOut = new FileOutputStream(OPERATION_LOG_PATH);
             FileOutputStream fileOtoOut = new FileOutputStream(PLAN_OTO_PATH);
        ) {

            Sheet meterWorkSheet = planOTOWorkbook.getSheet("ИИК");
            Sheet operationLogSheet = operationLog.getSheet("ОЖ");
            int operationLogLastRowNumber = operationLogSheet.getLastRowNum();
            int orderColumnNumber = excelFileService.findColumnIndex(meterWorkSheet, "Отчет бригады о выполнении ОТО");
            int deviceNumberColumnIndex;
            String taskorder = "";
            int[] columnIndexes = null;

            if (isDcChange) {
                columnIndexes = Arrays.stream(new String[]{
                                "Номер УСПД",
                                "Присоединение",
                                "Точка Учета",
                                "Место установки счетчика (Размещение счетчика)",
                                "Адрес",
                                "Марка счётчика",
                                "Номер счетчика"
                        })
                        .mapToInt(name -> excelFileService.findColumnIndex(meterWorkSheet, name))
                        .filter(index -> index >= 0)
                        .toArray();

                deviceNumberColumnIndex = columnIndexes[0];
            } else {
                deviceNumberColumnIndex = excelFileService.findColumnIndex(meterWorkSheet, "Номер счетчика");
            }


            CellStyle commonCellStyle = excelFileService.createCommonCellStyle(operationLog);
            CellStyle dateCellStyle = excelFileService.createDateCellStyle(operationLog, "dd.MM.YYYY", "Calibri");


            int addRow = 0;

            for (Row otoRow : meterWorkSheet) {
                String deviceNumber = excelFileService.getCellStringValue(otoRow.getCell(deviceNumberColumnIndex));
                Cell otoRowOrderCell = otoRow.getCell(orderColumnNumber);
                String logData = otoLog.getOrDefault(deviceNumber, "");
                if (!logData.isEmpty()) {
                    Row newRow = operationLogSheet.createRow(operationLogLastRowNumber + ++addRow);
                    excelFileService.copyRow(otoRow, newRow, orderColumnNumber, commonCellStyle, dateCellStyle);
                    taskorder = addOtoData(deviceNumber, logData, newRow, otoRow, deviceNumberColumnIndex, orderColumnNumber);
                    if (isDcChange) clearCellData(columnIndexes, newRow);
                }
            }

            if (isDcChange) {
                Sheet dcWorkSheet = planOTOWorkbook.getSheet("ИВКЭ");
                int orderDcSheetColumnNumber = excelFileService.findColumnIndex(dcWorkSheet, "Серийный номер концентратора");
                for (Row otoRow : dcWorkSheet) {
                    String deviceNumber = excelFileService.getCellStringValue(otoRow.getCell(orderDcSheetColumnNumber));
                    String logData = otoLog.getOrDefault(deviceNumber, "");
                    if (!logData.isEmpty()) {
                        int dcCurrentState = excelFileService.findColumnIndex(dcWorkSheet, "Состояние ИВКЭ");
                        Cell otoRowDcStateCell = otoRow.createCell(dcCurrentState);
                        Cell dcNumberCell = otoRow.getCell(orderDcSheetColumnNumber);
                        otoRowDcStateCell.setCellValue(taskorder);
                        dcNumberCell.setCellValue(logData.split("_")[1]);
                    }
                }
            }
            operationLog.write(fileOut);
            planOTOWorkbook.write(fileOtoOut);
            otoLog.clear();

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
    }

    private void clearCellData(int[] ints, Row row) {
        for (int i = 1; i < ints.length; i++) {
            row.getCell(i).setCellValue("");
        }
    }

    private String addOtoData(String deviceNumber, String logData, Row newLogRow, Row otoRow, int deviceNumberColumnIndex, int orderCellNumber) {
        String data = logData.substring(0, logData.indexOf("_"));
        String[] additionalData = logData.split("_");
        List<String> columns = getStrings(data);

        Cell date = newLogRow.getCell(16);
        excelFileService.setDateCellStyle(date);

        newLogRow.getCell(17).setCellValue(columns.get(0));
        newLogRow.getCell(18).setCellValue(columns.get(1));
        newLogRow.getCell(19).setCellValue("");
        newLogRow.getCell(20).setCellValue("Исполнитель"); //TODO: взять исполнителя из БД по chatId

        String taskOrder = straightFormattedCurrentDate + " -" + columns.get(2) + switch (data) {
            case "WK", "NOT", "SUPPLY" -> (additionalData.length > 1 ? " " + additionalData[1] : "");

            case "meterChange" -> {
                Object mountingDeviceNumber = parseMeterNumber(additionalData[2]);
                // Внесение номера Устройства в журнал "Контроль ПУ РРЭ"
                if (mountingDeviceNumber instanceof Long) {
                    otoRow.getCell(deviceNumberColumnIndex).setCellValue((Long) mountingDeviceNumber);
                } else {
                    otoRow.getCell(deviceNumberColumnIndex).setCellValue((String) mountingDeviceNumber);
                }
                yield deviceNumber + " (" + additionalData[1]
                        + " кВт) на " + additionalData[2] + " (" + additionalData[3] + " кВт). Причина замены: " + additionalData[4] + ".";
            }
            case "ttChange" ->
                    String.format("%s, номиналом %s, с классом точности %s, %sг.в. №АВС = %s, %s, %s. Причина замены: %s.",
                            additionalData[1], additionalData[2], additionalData[3], additionalData[4],
                            additionalData[5], additionalData[6], additionalData[7], additionalData[8]);
            case "dcChange" -> {
                otoRow.getCell(deviceNumberColumnIndex).setCellValue(deviceNumber);
                yield String.format("%s на концентратор № %s. Причина замены: %s.", deviceNumber, additionalData[1], additionalData[2]);
            }
            default -> null;
        };

        newLogRow.createCell(21).setCellValue(taskOrder);
        otoRow.getCell(orderCellNumber).setCellValue(taskOrder);
        return taskOrder;

//      newLogRow.createCell(22).setCellValue("Выполнено");   //TODO: добавить после реализации внесения корректировок в Горизонт либо БД
    }

    private static List<String> getStrings(String data) {
        Map<String, List<String>> fillingData = Map.of(
                "WK", List.of("Нет связи со счетчиком",
                        "Ошибка ключа - Вронгкей (сделана прошивка счетчика)",
                        " Сброшена ошибка ключа Вронгкей (счетчик не на связи)."),
                "NOT", List.of("Нет связи со счетчиком",
                        "Уточнение реквизитов ТУ (подана заявка на корректировку НСИ)", " НОТ."),
                "SUPPLY", List.of("Нет связи со счетчиком", "Восстановление схемы.", " Восстановление схемы подключения."),
                "meterChange", List.of("Нет связи со счетчиком", "Неисправность счетчика (счетчик заменен)", " Замена прибора учета №"),
                "ttChange", List.of("Повреждение ТТ\n", " - Повреждение ТТ (ТТ заменили)",
                        " Замена трансформаторов тока. Установлены трансформаторы "),
                "dcChange", List.of("Нет связи со всеми счетчиками\n", " - Повреждение концентратора (Концентратор заменили)",
                        " Замена концентратора №"
                ));
        return fillingData.get(data);
    }

    private String actionConfirmation() {
        StringBuilder resultStr = new StringBuilder("Выполнены следующие действия:\n");
        int lineCounter = 0;

        for (Map.Entry<String, String> entry : otoLog.entrySet()) {
            String key = entry.getKey();
            String[] str = entry.getValue().split("_");
            String actionType = str[0];
            List<String> strings = getStrings(actionType);

            resultStr.append(++lineCounter).append(". ").append(strings.get(2));
            switch (actionType) {
                case "meterChange" -> resultStr.append(String.format(
                        "%s с показаниями: %s\n на прибор учета № %s с показаниями: %s. Причина: %s.", key, str[1], str[2], str[3], str[4]));
                case "ttChange" -> resultStr.append(String.format(
                        "%s, номиналом %s, с классом точности %s, %sг.в. №АВС = %s, %s, %s.",
                        str[1], str[2], str[3], str[4], str[5], str[6], str[7]));
                case "dcChange" -> resultStr.append(String.format(
                        "%s на концентратор №%s. Причина: %s.", key, str[1], str[2]));
                default -> {
                    resultStr.append(String.format(" ПУ № %s.", key));
                    if (str.length > 1) resultStr.append(" ").append(str[str.length - 1]).append(".");
                }
            }
            resultStr.append("\n");
        }
        return resultStr.toString();
    }

    private void formingOtoLogWithDeviceChange(String deviceInfo, OtoType otoType) {
        String deviceNumber = deviceInfo.substring(0, deviceInfo.indexOf("_"));
        String changeType = switch (otoType) {
            case METER_CHANGE -> "meterChange";
            case TT_CHANGE -> "ttChange";
            case DC_CHANGE -> "dcChange";
            default -> "unknown";
        };
        otoLog.put(deviceNumber, changeType + deviceInfo.substring(deviceInfo.indexOf("_")));
    }

    private Map<String, String> getPhotoSavingPathFromExcel() {

        ExcelFileService excelFileService = new ExcelFileService();

        Map<String, String> eelToNtel = Map.of(
                "ЭЭЛ-1", "НТЭЛ-1",
                "НЭЭЛ-1", "НТЭЛ-1.1",
                "ЭЭЛ-2", "НТЭЛ-2",
                "ЭЭЛ-2.1", "НТЭЛ-2.1",
                "ЭЭЛ-3", "НТЭЛ-3",
                "ЭЭЛ-3.1", "НТЭЛ-3.1",
                "ЭЭЛ-3.2", "НТЭЛ-3.2",
                "ЭЭЛ-3.3", "НТЭЛ-3.3",
                "ЭЭЛ-4", "НТЭЛ-4"
        );

        Map<String, String> paths = null;
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH))) {
            paths = new HashMap<>();
            Sheet iikSheet = planOTOWorkbook.getSheet("ИИК");
            int meterNumberColumnIndex = excelFileService.findColumnIndex(iikSheet, "Номер счетчика");
            int eelColumnIndex = excelFileService.findColumnIndex(iikSheet, "ЭЭЛ");
            int stationColumnIndex = excelFileService.findColumnIndex(iikSheet, "Железнодорожная станция");
            int substationColumnIndex = excelFileService.findColumnIndex(iikSheet, "ТП/КТП");
            int meterPointIndex = excelFileService.findColumnIndex(iikSheet, "Точка Учета");
            for (Row row : iikSheet) {
                String meterNum = excelFileService.getCellStringValue(row.getCell(meterNumberColumnIndex));
                if (meterNum != null) {
                    paths.put(meterNum,
                            eelToNtel.get(row.getCell(eelColumnIndex).getStringCellValue()) + "\\" +
                                    row.getCell(stationColumnIndex).getStringCellValue() + "\\" +
                                    row.getCell(substationColumnIndex).getStringCellValue() + "\\" +
                                    row.getCell(meterPointIndex).getStringCellValue());
                }
            }
        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
        return paths;
    }

    // Метод для проверки и преобразования номера счетчика
    private Object parseMeterNumber(String meterNumberStr) {
        try {
            return Long.parseLong(meterNumberStr);
        } catch (NumberFormatException e) {
            return meterNumberStr;
        }
    }


    private void register(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Do you really want to register?");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);
        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);

        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
