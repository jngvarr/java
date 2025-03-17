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

    public enum UserState {
        WAITING_FOR_COUNTER_PHOTO,
        WAITING_FOR_DC_PHOTO,
        WAITING_FOR_TT_PHOTO,
        MANUAL_INSERT,
        WAITING_FOR_METER_READING,
        NONE;
    }

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
                    "Демонтаж концентратора", "dcRemove"));

    // Карта для хранения состояния диалога по chatId

    private Map<Long, UserState> userStates = new HashMap<>();
    private Map<Long, OtoIIKType> otoIIKTypeMap = new HashMap<>();
    private Map<Long, OtoDC> otoDCMap = new HashMap<>();
    private Map<String, String> otoIIKLog = new HashMap<>();
    private List<String> deviceInfo = new ArrayList<>();

    // Карта для хранения информации о фото, ожидающих подтверждения
    private Map<Long, PendingPhoto> pendingPhotos = new HashMap<>();

    public enum OtoIIKType {
        WK_DROP,
        METER_CHANGE,
        SET_NOT,
        SUPPLY_RESTORING,
        TT_CHANGE
    }

    public enum OtoDC {
        DC_CHANGE,
        SET_NOT,
        SUPPLY_RESTORING
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
            "Восстановление питания", "powerSupplyRestoring",
            "Другие работы", "otherDC");

    private Map<String, String> confirmMenu = Map.of(
            "Подтвердить выполнение", "confirm",
            "Отменить выполнение", "cancel");

    private Map<String, String> savingPaths = getPhotoSavingPathFromExcel();
    private Map<String, String> CompleteButton = Map.of("Завершить загрузку данных", "LOADING_COMPLETE");
    private int photoCounter;
    private String changeTTInfo = "";

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
        if (OtoIIKType.METER_CHANGE.equals(otoIIKTypeMap.get(chatId)) && photoCounter >= 2 ||
                OtoIIKType.TT_CHANGE.equals(otoIIKTypeMap.get(chatId)) && photoCounter >= 3) {
            return;
        }

        if (OtoIIKType.METER_CHANGE.equals(otoIIKTypeMap.get(chatId)) || OtoIIKType.TT_CHANGE.equals(otoIIKTypeMap.get(chatId))) {
            photoCounter++;
        }

        // Проверяем, есть ли подпись к фото
        String manualInput = update.getMessage().getCaption();

        // Если фото не запрашивалось
        if (!userStates.containsKey(chatId)) {
            sendMessage(chatId, "Фото не запрашивалось. Если хотите начать, нажмите /start");
            return;
        }

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
            // Files.deleteIfExists(tempFilePath);
            String barcodeText = "";
            if (userStates.get(chatId).equals(UserState.WAITING_FOR_COUNTER_PHOTO)) {
                // 4. Декодируем штрихкод
                barcodeText = preparingPhotoService.decodeBarcode(bufferedImage);
                if (barcodeText == null) {
                    barcodeText = preparingPhotoService.decodeBarcode(preparingPhotoService.resizeImage(bufferedImage,
                            bufferedImage.getWidth() * 2, bufferedImage.getHeight() * 2));
                }
                if (barcodeText == null) {
                    barcodeText = preparingPhotoService.decodeBarcode(preparingPhotoService.convertToGrayscale(bufferedImage));
                }
            } else if (userStates.get(chatId).equals(UserState.WAITING_FOR_TT_PHOTO)) {
                barcodeText = changeTTInfo.substring(0, changeTTInfo.indexOf("_"));
            }

            // 5. Определяем тип фото (счётчик, тт или концентратор)
            String type = switch (userStates.get(chatId)) {
                case WAITING_FOR_COUNTER_PHOTO -> "counter";
                case WAITING_FOR_DC_PHOTO -> "concentrator";
                case WAITING_FOR_TT_PHOTO -> "tt";
                default -> throw new IllegalStateException("Неизвестный тип оборудования: " + userStates.get(chatId));
            };

            // 6. Создаём объект для хранения фото
            PendingPhoto pendingPhoto = new PendingPhoto(type, tempFilePath, barcodeText);
            pendingPhotos.put(chatId, pendingPhoto);
            if (type.equals("counter")) {
                if (manualInput != null) pendingPhoto.setAdditionalInfo(manualInput.trim());

                // 7. Если штрихкод найден и есть показания – сразу сохраняем
                if (barcodeText != null && pendingPhoto.getAdditionalInfo() != null) {
                    savePhoto(chatId, pendingPhoto);
                } else if (barcodeText == null) {
                    sendMessage(chatId, "Штрихкод не найден. Введите номер ПУ вручную:");
                    userStates.put(chatId, UserState.MANUAL_INSERT);
                } else {
                    sendMessage(chatId, "Введите показания счётчика:");
                    userStates.put(chatId, UserState.WAITING_FOR_METER_READING);
                }
            } else if (type.equals("tt")) {
                if (manualInput != null) {
                    pendingPhoto.setAdditionalInfo(manualInput);
                    savePhoto(chatId, pendingPhoto);

                } else {
                    photoCounter--;
                    sequenceNumber--;
                    sendMessage(chatId, "Номер трансформатора не найден. Повторите предыдущее действие.");
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

        if (userStates.get(chatId) == UserState.MANUAL_INSERT) {
            handleManualInsert(chatId, msgText);
            return;
        }

        if (userStates.get(chatId) == UserState.WAITING_FOR_METER_READING) {
            handleMeterReading(chatId, msgText);
            return;
        }

        OtoIIKType currentOtoIIKType = otoIIKTypeMap.get(chatId);

        // Обработка смены счетчика
        if (currentOtoIIKType == OtoIIKType.METER_CHANGE) {
            handleMeterChange(chatId, msgText);
            return;
        }

        // Обработка смены ТТ (Трансформаторов тока)
        if (currentOtoIIKType == OtoIIKType.TT_CHANGE) {
            handleTTChange(chatId, msgText);
            return;
        }

        // Обработка WK_DROP, SET_NOT, SUPPLY_RESTORING
        if (Map.of(OtoIIKType.WK_DROP, "WK_", OtoIIKType.SET_NOT, "NOT_", OtoIIKType.SUPPLY_RESTORING, "SUPPLY_")
                .containsKey(currentOtoIIKType)) {
            handleOtherOtoIIKTypes(chatId, msgText, currentOtoIIKType);
            return;
        }

//        Map<OtoIIKType, String> otoIIKStringMap = Map.of(
//                OtoIIKType.WK_DROP, "WK_",
//                OtoIIKType.SET_NOT, "NOT_",
//                OtoIIKType.TT_CHANGE, "TT_",
//                OtoIIKType.SUPPLY_RESTORING, "SUPPLY_");



        if (currentOtoIIKType == OtoIIKType.TT_CHANGE) {
            changeTTInfo += (update.getMessage().getText()) + "_";
            sequenceNumber++;
            switch (sequenceNumber) {
                case 1 -> sendMessage(chatId, "Введите тип трансформаторов тока (пример: ТШП-0,66): ");
                case 2 -> sendMessage(chatId, "Введите коэффициент трансформации (пример: 300/5): ");
                case 3 -> sendMessage(chatId, "Введите класс точности (пример: 0,5 или 0,5S): ");
                case 4 -> sendMessage(chatId, "Введите год выпуска трансформаторов (пример: 2025): ");
                case 5 -> {
                    if (!userStates.isEmpty()) {
                        sendMessage(chatId, "Прикрепите фото ТТ ф.A и введите его номер: ");
                    } else sendMessage(chatId, "Введите номер ТТ ф.A: ");
                }
                case 6 -> {
                    if (!userStates.isEmpty()) {
                        sendMessage(chatId, "Прикрепите фото ТТ ф.B и введите его номер: ");
                    } else sendMessage(chatId, "Введите номер ТТ ф.B: ");
                }
                case 7 -> {
                    if (!userStates.isEmpty()) {
                        sendMessage(chatId, "Прикрепите фото ТТ ф.C и введите его номер: ");
                    } else sendMessage(chatId, "Введите номер ТТ ф.C: ");
                }
                case 8 -> {
                    String deviceNumber = changeTTInfo.substring(0, changeTTInfo.indexOf("_"));
                    otoIIKLog.put(deviceNumber, "ttChange" + changeTTInfo.substring(changeTTInfo.indexOf("_")));
                    sendTextMessage(actionConfirmation(otoIIKLog), confirmMenu, chatId, 2);
                }
            }
            return;
        }
        // Обработка остальных текстовых сообщений
        switch (msgText) {
            case "/start" -> handleStartCommand(chatId, update.getMessage().getChat().getFirstName());
            case "/help" -> sendMessage(chatId, HELP);
            case "/register" -> registerUser(chatId);
            default -> sendMessage(chatId, "Команда не распознана. Попробуйте еще раз.");
        }
    }

    private void handleMeterChange(long chatId, String deviceNumber) {
        deviceInfo.add(deviceNumber);
        ++sequenceNumber;
        if (sequenceNumber < 2) {
            sendMessage(chatId, "Введите номер и показания устанавливаемого ПУ.");
        } else {
            formingOtoIikLogWithMeterChange(deviceInfo);
            sendTextMessage(actionConfirmation(otoIIKLog), confirmMenu, chatId, 2);
        }
    }

    private void handleMeterReading(long chatId, String msgText) {
            String deviceIndication = msgText.trim();
            PendingPhoto pending = pendingPhotos.get(chatId);
            if (pending != null) {
                pending.setAdditionalInfo(deviceIndication);
                savePhoto(chatId, pending);
            } else {
                sendMessage(chatId, "Ошибка: нет ожидающих фото для привязки показаний.");
            }
    }

    private void handleManualInsert(long chatId, String deviceNumber) {
        deviceNumber = deviceNumber.trim();
        PendingPhoto pending = pendingPhotos.get(chatId);
        if (pending != null) {
            pending.setDeviceNumber(deviceNumber);
            savePhoto(chatId, pending);
        } else {
            sendMessage(chatId, "Ошибка: нет ожидающих фото для привязки данных.");
        }
    }


    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (callbackData) {
            case "newTU" -> {
                sendTextMessage(NEW_TU, modes.get("newTU"), chatId, 1);
            }
            case "pto" -> {
                sendTextMessage(PTO, modes.get("pto"), chatId, 2);
            }
            case "oto" -> {
                sendTextMessage(OTO, modes.get("oto"), chatId, 2);
            }
            // Обработка выбора для счетчика и концентратора
            case "ptoIIK" -> {
                sendMessage(chatId, "Пожалуйста, загрузите фото счетчика.");
                userStates.put(chatId, UserState.WAITING_FOR_COUNTER_PHOTO);
            }
            case "ptoIVKE" -> {
                sendMessage(chatId, "Пожалуйста, загрузите фото концентратора.");
                userStates.put(chatId, UserState.WAITING_FOR_DC_PHOTO);
            }
            case "otoIIK" -> {
                sendTextMessage("Выберите вид ОТО ИИК: ", otoIIKButtons, chatId, 2);
            }
            case "otoIVKE" -> {
                sendTextMessage("Выберите вид ОТО ИВКЭ: ", otoIVKEButtons, chatId, 2);
            }

            case "wkDrop" -> {
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoIIKTypeMap.put(chatId, OtoIIKType.WK_DROP);
            }

            case "setNot" -> {
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoIIKTypeMap.put(chatId, OtoIIKType.SET_NOT);
            }

            case "powerSupplyRestoring" -> {
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoIIKTypeMap.put(chatId, OtoIIKType.SUPPLY_RESTORING);
            }

            case "meterChange", "ttChange" -> {
                String value1 = "";
                String value2 = "";
                if (callbackData.equals("meterChange")) {
                    value1 = "meterChangeWithPhoto";
                    value2 = "meterChangeWithoutPhoto";
                } else {
                    value1 = "ttChangeWithPhoto";
                    value2 = "ttChangeWithOutPhoto";
                }
                sendTextMessage("Вид передачи данных: ",
                        Map.of("С приложением фото.", value1,
                                "Без приложения фото.", value2), chatId, 2);
            }

            case "ttChangeWithPhoto", "ttChangeWithOutPhoto" -> {
                if ("ttChangeWithPhoto".equals(callbackData))
                    userStates.put(chatId, UserState.WAITING_FOR_TT_PHOTO);
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoIIKTypeMap.put(chatId, OtoIIKType.TT_CHANGE);
            }

            case "dcChangeWithPhoto", "dcChangeWithOutPhoto" -> {
                if ("dcChangeWithOutPhoto".equals(callbackData))
                    userStates.put(chatId, UserState.WAITING_FOR_TT_PHOTO);
                sendMessage(chatId, "Введите номер концентратора: ");
                otoDCMap.put(chatId, OtoDC.DC_CHANGE);
            }

            case "meterChangeWithPhoto", "meterChangeWithoutPhoto" -> {
                String textToSend = "";
                otoIIKTypeMap.put(chatId, OtoIIKType.METER_CHANGE);
                if ("meterChangeWithPhoto".equals(callbackData)) {
                    textToSend = "Прикрепите фото демонтируемого прибора учета: ";
                    userStates.put(chatId, UserState.WAITING_FOR_COUNTER_PHOTO);
                } else textToSend = "Введите номер и показания демонтируемого прибора учета: \n" +
                        "например: 7200123456_7890";
                sendMessage(chatId, textToSend);
            }

            case "LOADING_COMPLETE" -> {
                sendTextMessage(actionConfirmation(otoIIKLog), confirmMenu, chatId, 2);
            }

            case "confirm", "cancel" -> {
                String textToSend;
                if ("confirm".equals(callbackData)) {
                    textToSend = "Информация сохранена.";
                    sendMessage(chatId, "Подождите, идёт загрузка данных...");
                    operationLogFilling(otoIIKLog, true);
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

    private void clearData() {
        otoIIKLog.clear();
        sequenceNumber = 0;
        userStates.clear();
        otoIIKTypeMap.clear();
        photoCounter = 0;
    }


    private void handleStartCommand(long chatId, String firstName) {
//        String welcomeMessage = String.format("Приветствую тебя, пользователь %s, Что будем делать?", firstName);
        log.info("Replied to user: {}", firstName);
//        sendMessage(chatId, welcomeMessage, null);

        sendTextMessage(MAIN_MENU, Map.of(
                "ПТО", "pto",
                "ОТО", "oto",
                "Монтаж / демонтаж ТУ", "newTU"
        ), chatId, 1);
    }

    private static final Map<OtoIIKType, String> PHOTO_SUBDIRS_NAME = Map.of(
            OtoIIKType.METER_CHANGE, "Замена ПУ",
            OtoIIKType.TT_CHANGE, "Замена ТТ"
    );

    private void savePhoto(long chatId, PendingPhoto pending) {
        try {
            OtoIIKType operationType = otoIIKTypeMap.get(chatId);
            Path userDir = Paths.get(createSavingPath(operationType, pending));

            Files.createDirectories(userDir);

            String newFileName = createNewFileName(pending, operationType);
            Path destination = userDir.resolve(newFileName);

            // Сохранение
            Files.move(pending.getTempFilePath(), destination, StandardCopyOption.REPLACE_EXISTING);
            sendMessage(chatId, "Фото сохранено!\nФайл: " + newFileName);

            pendingPhotos.remove(chatId);

            if (photoCounter >= 2) {
                sendTextMessage("Фото сохранены, пожалуйста завершите загрузку.", CompleteButton, chatId, 1);
                return;
            }

            if (otoIIKTypeMap.get(chatId).equals(OtoIIKType.METER_CHANGE)) {
                sendMessage(chatId, "Прикрепите фото устанавливаемого прибора учета:\n");
            } else
                sendTextMessage("Заргрузите следующее фото или закончите загрузку.", CompleteButton, chatId, 1);

        } catch (
                Exception e) {
            log.error("Ошибка сохранения фото: " + e.getMessage());
            sendMessage(chatId, "Произошла ошибка при сохранении фото.");
        }
    }

    private String createSavingPath(OtoIIKType operationType, PendingPhoto pending) {
        String baseDir = PHOTO_PATH + File.separator;

        if (operationType != null && PHOTO_SUBDIRS_NAME.containsKey(operationType)) {
            baseDir += PHOTO_SUBDIRS_NAME.get(operationType) + File.separator;
        }
        String path = savingPaths.getOrDefault(pending.getDeviceNumber(), "unknown");
        String resultPath = !PHOTO_SUBDIRS_NAME.containsKey(operationType) ? path.substring(0, path.lastIndexOf("\\")) : path;

        if (photoCounter == 1) chgePath = resultPath;
        if (photoCounter == 2) resultPath = chgePath;
        return baseDir + resultPath;
    }

    private String createNewFileName(PendingPhoto pending, OtoIIKType operationType) {
        String additionalInfo = pending.getAdditionalInfo();
        String deviceNumber = (pending.getDeviceNumber() != null) ? pending.getDeviceNumber() : "";
        String meterIndicationOrTtNumber = (additionalInfo != null) ? switch (operationType) {
            case METER_CHANGE -> "_" + additionalInfo;
            case TT_CHANGE -> "_(" + additionalInfo + ")";
            default -> "";
        } : "";

        if (operationType != null && operationType.equals(OtoIIKType.METER_CHANGE)) {
            deviceInfo.add(deviceNumber + meterIndicationOrTtNumber);
            formingOtoIikLogWithMeterChange(deviceInfo);
        }

        return formattedCurrentDate + "_" + getSavingPhotoPrefix(pending.getType()) + deviceNumber +
                meterIndicationOrTtNumber + getSavingPhotoSuffix(operationType) + ".jpg";
    }

    private String getSavingPhotoSuffix(OtoIIKType operationType) {
        if (operationType != null && operationType.equals(OtoIIKType.METER_CHANGE)) return switch (photoCounter) {
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


    private void registerUser(long chatId) {
//        InlineKeyboardMarkup keyboardMarkup = createInlineKeyboardMarkup(Map.of(
//                "Yes", YES_BUTTON,
//                "No", NO_BUTTON
//        ));
        sendTextMessage("Do you really want to register?",
                Map.of("Yes", YES_BUTTON, "No", NO_BUTTON), chatId, 2);
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(Map<String, String> buttons) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        buttons.forEach((label, callbackData) -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(label);
            button.setCallbackData(callbackData);
            keyboard.add(List.of(button));
        });

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
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

    private void operationLogFilling(Map<String, String> opLog, boolean isIikLog) {
        if (opLog.isEmpty()) return;
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             Workbook operationLog = new XSSFWorkbook(new FileInputStream(OPERATION_LOG_PATH));
             FileOutputStream fileOut = new FileOutputStream(OPERATION_LOG_PATH);
             FileOutputStream fileOtoOut = new FileOutputStream(PLAN_OTO_PATH);
        ) {
            Sheet workSheet = isIikLog ? planOTOWorkbook.getSheet("ИИК") : planOTOWorkbook.getSheet("ИВКЭ");
            Sheet operationLogSheet = operationLog.getSheet("ОЖ");
            int operationLogLastRowNumber = operationLogSheet.getLastRowNum();
            int meterNumberColumnIndex = excelFileService.findColumnIndex(workSheet, "Номер счетчика");
            int orderColumnNumber = excelFileService.findColumnIndex(workSheet, "Отчет бригады о выполнении ОТО");

            CellStyle commonCellStyle = excelFileService.createCommonCellStyle(operationLog);
            CellStyle dateCellStyle = excelFileService.createDateCellStyle(operationLog, "dd.MM.YYYY", "Calibri");

            int addRow = 0;
            for (Row row : workSheet) {
                String meterNumber = excelFileService.getCellStringValue(row.getCell(meterNumberColumnIndex));
                String logData = opLog.getOrDefault(meterNumber, "");
                if (!logData.isEmpty()) {
                    Row newRow = operationLogSheet.createRow(operationLogLastRowNumber + ++addRow);
                    excelFileService.copyRow(row, newRow, orderColumnNumber, commonCellStyle, dateCellStyle);
                    addOtoData(logData, newRow, row, meterNumberColumnIndex, orderColumnNumber);
                }
            }
            operationLog.write(fileOut);
            planOTOWorkbook.write(fileOtoOut);
            otoIIKLog.clear();

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
    }

    private void addOtoData(String logData, Row newRow, Row otoRow, int meterNumColIndex, int orderColIndex) {
        String data = logData.substring(0, logData.indexOf("_"));
        String[] additionalData = logData.split("_");
        List<String> columns = getStrings(data);

        Cell date = newRow.getCell(16);
        excelFileService.setDateCellStyle(date);

        newRow.getCell(17).setCellValue(columns.get(0));
        newRow.getCell(18).setCellValue(columns.get(1));
        newRow.getCell(20).setCellValue("Исполнитель"); //TODO: взять исполнителя из БД по chatId

        String taskOrder = straightFormattedCurrentDate + " -" + columns.get(2) + switch (data) {
            case "WK", "NOT", "SUPPLY" -> (additionalData.length > 1 ? " " + additionalData[1] : "");

            case "meterChange" -> {
                Object mountingMeterNumber = parseMeterNumber(additionalData[2]);
                // Внесение номера счётчика в журнал "Контроль ПУ РРЭ"
                if (mountingMeterNumber instanceof Long) {
                    otoRow.getCell(meterNumColIndex).setCellValue((Long) mountingMeterNumber);
                } else {
                    otoRow.getCell(meterNumColIndex).setCellValue((String) mountingMeterNumber);
                }
                yield excelFileService.getCellStringValue(newRow.getCell(13)) + " (" + additionalData[1]
                        + " кВт) на " + additionalData[2] + " (" + additionalData[3] + " кВт).";
            }
            case "ttChange" -> String.format("%s, номиналом %s, с классом точности %s, %sг.в. №АВС = %s, %s, %s.",
                    additionalData[1], additionalData[2], additionalData[3], additionalData[4],
                    additionalData[5], additionalData[6], additionalData[7]);
            default -> null;
        };

        newRow.createCell(21).setCellValue(taskOrder);
        otoRow.getCell(orderColIndex).setCellValue(taskOrder);

//      newRow.createCell(22).setCellValue("Выполнено");   //TODO: добавить после реализации внесения корректировок в Горизонт либо БД
    }

    private static List<String> getStrings(String data) {
        Map<String, List<String>> fillingData = Map.of(
                "WK", List.of("Нет связи со счетчиком",
                        "Ошибка ключа - Вронгкей (сделана прошивка счетчика)",
                        " Сброшена ошибка ключа Вронгкей (счетчик не на связи)."),
                "NOT", List.of("Нет связи со счетчиком",
                        "Уточнение реквизитов ТУ (подана заявка на корректировку НСИ)", " НОТ."),
                "SUPPLY", List.of("Нет связи со счетчиком", "Восстановление схемы.", " Восстановление схемы подключения."),
                "meterChange", List.of("Нет связи со счетчиком", "Неисправность счетчика (счетчик заменен)", " Замена прибора учета № "),
                "ttChange", List.of("Повреждение ТТ\n", " - Повреждение ТТ (ТТ заменили)",
                        " Замена трансформаторов тока. Установлены трансформаторы "));
        return fillingData.get(data);
    }

    private String actionConfirmation(Map<String, String> otoIIKLog) {
        StringBuilder resultStr = new StringBuilder("Выполнены следующие действия:\n");
        int lineCounter = 0;

        for (Map.Entry<String, String> entry : otoIIKLog.entrySet()) {
            String[] str = entry.getValue().split("_");
            String actionType = str[0];
            String[] key = entry.getKey().split("_");
            List<String> strings = getStrings(actionType);

            resultStr.append(++lineCounter).append(". ").append(strings.get(2));
            switch (actionType) {

                case "meterChange" -> resultStr.append(String.format(
                        "%s с показаниями: %s\n на прибор учета № %s с показаниями: %s", key[0], str[1], str[2], str[3]));
                case "ttChange" -> resultStr.append(String.format(
                        "%s, номиналом %s, с классом точности %s, %sг.в. №АВС = %s, %s, %s.",
                        str[1], str[2], str[3], str[4], str[5], str[6], str[7]));
                default -> {
                    resultStr.append(String.format(" ПУ № %s.", key[0]));
                    if (str.length > 1) resultStr.append(" ").append(str[str.length - 1]).append(".");
                }
            }
            resultStr.append("\n");
        }
        return resultStr.toString();
    }

    private void formingOtoIikLogWithMeterChange(List<String> deviceInfo) {
        String[] deviceNumber = deviceInfo.get(0).split("_");
        otoIIKLog.put(deviceNumber[0], "meterChange_" + deviceNumber[1] + "_" + deviceInfo.get(1));
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
