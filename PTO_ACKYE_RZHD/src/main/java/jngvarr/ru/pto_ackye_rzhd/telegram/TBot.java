package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.config.BotConfig;
import jngvarr.ru.pto_ackye_rzhd.entities.User;
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
import java.util.stream.Stream;

import static jngvarr.ru.pto_ackye_rzhd.telegram.FileManagement.*;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;


@Data
@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
public class TBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final TBotService tBotService;
    private UserServiceImpl userService;
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";
    private List<Message> sendMessages = new ArrayList<>();
    private final ExcelFileService excelFileService;
    private final PreparingPhotoService preparingPhotoService;
    // Карта для хранения информации о фото, ожидающих подтверждения
    private Map<Long, PendingPhoto> pendingPhotos = new HashMap<>();
    private Map<String, String> savingPaths;

    public enum ProcessState {
        WAITING_FOR_METER_PHOTO,
        WAITING_FOR_DC_PHOTO,
        WAITING_FOR_TT_PHOTO,
        MANUAL_INSERT_METER_NUMBER,
        MANUAL_INSERT_METER_INDICATION,
        IIK_WORKS,
        DC_WORKS,
        IIK_MOUNT,
        DC_MOUNT
    }

    private void handleStartCommand(long chatId, String firstName) {
        sendTextMessage(MAIN_MENU, startMenuButtons, chatId, 1);
    }

    // Мапа для хранения состояния диалога по chatId
    private Map<Long, ProcessState> processStates = new HashMap<>();
    private Map<Long, OtoType> otoTypes = new HashMap<>();
    private Map<String, String> otoLog = new HashMap<>();
    private Map<Long, PhotoState> photoStates = new HashMap<>();
    private Map<OtoType, String> PHOTO_SUBDIRS_NAME = Map.of(
            OtoType.METER_CHANGE, "Замена ПУ",
            OtoType.TT_CHANGE, "Замена ТТ",
            OtoType.DC_CHANGE, "Замена концентратора"
    );

    public enum OtoType {
        WK_DROP, METER_CHANGE, SET_NOT, SUPPLY_RESTORING, TT_CHANGE, DC_CHANGE, DC_RESTART
    }

    private boolean isPTO;
    String chgePath;
    private int sequenceNumber = 0;
    private String processInfo = "";
    private boolean isDcLocation;


    public TBot(BotConfig config, TBotService tBotService, UserServiceImpl userService, ExcelFileService excelFileService, PreparingPhotoService preparingPhotoService) {
        super(config.getBotToken());
        this.config = config;
        this.tBotService = tBotService;
        this.userService = userService;
        this.excelFileService = excelFileService;
        this.preparingPhotoService = preparingPhotoService;
        savingPaths = excelFileService.getPhotoSavingPathFromExcel();//TODO подумать

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать работу"));
        listOfCommands.add(new BotCommand("/help", "немного информации по использованию бота"));
        listOfCommands.add(new BotCommand("/stop", "сбросить всё, начать заново"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot`s command list" + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            return;
        }
        User user = userService.getUserById(chatId);
        if (user == null) {
            sendMessage(chatId, "Пожалуйста пройдитете регистрацию и дождитесь валидации администратора");
        } else if (user.isAccepted()) {
            if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    handleTextMessage(update);
                } else if (update.getMessage().hasPhoto()) {
                    handlePhotoMessage(update);
                }
            } else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
            }
        }
    }

    private void handlePhotoMessage(Update update) {
        long chatId = update.getMessage().getChatId();

        // Проверяем, есть ли подпись к фото
        String manualInput = update.getMessage().getCaption();

        // Если фото не запрашивалось
        if (!processStates.containsKey(chatId)) {
            sendMessage(chatId, "Фото не запрашивалось. Если хотите начать, нажмите /start");
            return;
        }
        sendMessage(chatId, "Подождите, идёт обработка фото....");
        ProcessState currentState = processStates.get(chatId);
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
                    processStates.put(chatId, ProcessState.MANUAL_INSERT_METER_NUMBER);
                    return;
                }
                if (manualInput == null) {
                    sendMessage(chatId, "Показания счетчика не введены. Введите показания счётчика:");
                    processStates.put(chatId, ProcessState.MANUAL_INSERT_METER_INDICATION);
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
            } else {
                if (manualInput != null) {
                    pendingPhoto.setDeviceNumber(manualInput.trim());
                    savePhoto(chatId, pendingPhoto);
                } else {
                    pendingPhoto.setAdditionalInfo("Данные не требуются.");
                    PhotoState photoState = photoStates.get(chatId);
                    OtoType otoType = otoTypes.get(chatId);
                    sendMessage(chatId, "❌ Номер концентратора не обнаружен!! Пожалуйста введите еще раз:");
                    processStates.put(chatId, ProcessState.MANUAL_INSERT_METER_NUMBER);
//                    sendNextPhotoInstruction(chatId, photoState.getNextPhotoType(otoType));
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
        ProcessState processState = processStates.get(chatId);
        OtoType otoType = otoTypes.get(chatId);
        switch (msgText) {
            case "/start" -> {
                handleStartCommand(chatId, update.getMessage().getChat().getFirstName());
                clearData();
                return;
            }
            case "/help" -> {
                sendMessage(chatId, HELP);
                return;
            }
            case "/register" -> {
                registerUser(chatId);
                return;
            }
            case "/stop" -> {
                sendMessage(chatId, "Работа прервана, для продолжения нажмите /start");
                clearData();
                return;
            }
            case "/accept" -> {

                sendMessage(chatId, "Работа прервана, для продолжения нажмите /start");
                clearData();
                return;
            }
        }

        if (processState != null) {
            switch (processState) {
                case MANUAL_INSERT_METER_NUMBER, MANUAL_INSERT_METER_INDICATION -> {
                    handleManualInsert(chatId, msgText);
                    return;
                }
                case IIK_MOUNT, DC_MOUNT -> {
                    handleEquipmentMount(chatId, msgText, processState);
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
                case WK_DROP, SET_NOT, SUPPLY_RESTORING, DC_RESTART -> {
                    handleOtherOtoTypes(chatId, msgText);
                    return;
                }
            }
        }
        sendMessage(chatId, "Команда не распознана. Попробуйте еще раз.");

    }

    private void handleOtherOtoTypes(long chatId, String msgText) {
        OtoType currentOtoType = otoTypes.get(chatId);
        String messageText = msgText.trim();

        switch (currentOtoType) {
            case WK_DROP -> {
                otoLog.put(messageText, "WK_");
                sendTextMessage("Введите номер следующего прибора учета или закончите ввод.", CompleteButton, chatId, 1);
            }
            case SET_NOT -> {
                processInfo += msgText + "_";
                if (sequenceNumber == 0) {
                    if (ProcessState.DC_WORKS.equals(processStates.get(chatId))) {
                        sendMessage(chatId, "Введите причину отключения: ");
                    } else {
                        chooseNotType(chatId);
                    }
                    sequenceNumber++;
                } else {
                    formingOtoLog(processInfo, currentOtoType);
                    sendTextMessage(actionConfirmation(chatId), confirmMenu, chatId, 2);
                }
            }
            case SUPPLY_RESTORING, DC_RESTART -> {
                if (currentOtoType.equals(OtoType.SUPPLY_RESTORING)) {
                    processInfo += msgText + "_";
                    if (sequenceNumber == 0) {
                        sendMessage(chatId, "Опишите причину неисправности: ");
                        sequenceNumber++;
                    } else {
                        formingOtoLog(processInfo, currentOtoType);
                        sendTextMessage(actionConfirmation(chatId), confirmMenu, chatId, 2);
                    }
                } else {
                    otoLog.put(messageText, "dcRestart_");
                    sendTextMessage(actionConfirmation(chatId), confirmMenu, chatId, 2);
                }
            }
        }
    }

    private void chooseNotType(Long chatId) {
        sendTextMessage("Выберите причину отключения: ",
                Map.of(
                        "Потребитель отключен.", "NOT",
                        "Сезонный потребитель.", "seasonNOT",
                        "Низкий уровень PLC сигнала", "lowPLC",
                        "Прибор учета демонтирован (НОТ3)", "NOT3",
                        "Прибор учета сгорел (НОТ2)", "NOT2",
                        "Местонахождения ПУ неизвестно (НОТ1 украден?)", "NOT1"),
                chatId, 1);
    }

    private void handleEquipmentChange(long chatId, String msgText, OtoType otoType) {
        Map<Integer, String> replacedEquipmentData = replacedEquipmentDatum.get(otoType);
        if (msgText != null && !msgText.trim().isEmpty()) {
            processInfo += msgText + "_";
        }
        if (sequenceNumber < replacedEquipmentData.size()) {
            if (processStates.get(chatId).equals(ProcessState.WAITING_FOR_TT_PHOTO) && sequenceNumber == 4) {
                sendMessage(chatId, "📸 Прикрепите фото **ТТ фазы A** и введите его номер:");
            } else {
                sendMessage(chatId, replacedEquipmentData.get(sequenceNumber));
            }
            sequenceNumber++;
        } else concludeDeviceOperation(chatId);
    }

    private void handleEquipmentMount(long chatId, String msgText, ProcessState state) {
        Map<Integer, String> mountedEquipmentData = mountedEquipmentDatum.get(state);
        if (msgText != null && !msgText.trim().isEmpty()) {
            processInfo += msgText + "_";
        }
        if (ProcessState.IIK_MOUNT.equals(processStates.get(chatId)) && sequenceNumber == 0) {
            String path = savingPaths.get(msgText);
            if (path != null) {
                sequenceNumber += 2;
                addPathToProcessInfo(path);
            }
        }
        if (sequenceNumber < mountedEquipmentData.size()) {
            sendMessage(chatId, mountedEquipmentData.get(sequenceNumber));
            sequenceNumber++;
        } else concludeDeviceOperation(chatId);
    }

    private void addPathToProcessInfo(String path) {
        String[] pathParts = path.split("\\\\");
        processInfo = processInfo + pathParts[pathParts.length - 2] + "_" + pathParts[pathParts.length - 1] + "_";
        isDcLocation = true;
    }

    private void handleManualInsert(long chatId, String deviceNumber) {
        String manualInput = deviceNumber.trim();
        PendingPhoto pending = pendingPhotos.get(chatId);
        if (pending != null) {
            if (processStates.get(chatId).equals(ProcessState.MANUAL_INSERT_METER_INDICATION)) {
                pending.setAdditionalInfo(manualInput);
            } else {
                pending.setDeviceNumber(manualInput);
            }
            boolean isDataFull = pending.getDeviceNumber() != null && pending.getAdditionalInfo() != null;
            if (isDataFull) {
                savePhoto(chatId, pending);
            } else if (pending.getDeviceNumber() == null) {
                sendMessage(chatId, "Заводской номер не найден. Введите номер вручную:");
                processStates.put(chatId, ProcessState.MANUAL_INSERT_METER_NUMBER);
            } else {
                sendMessage(chatId, "Показания счетчика не введены. Введите показания счётчика:");
                processStates.put(chatId, ProcessState.MANUAL_INSERT_METER_INDICATION);
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
                isPTO = true;
                sendTextMessage(PTO, modes.get(callbackData), chatId, 2);
            }
            case "oto" -> {
                sendTextMessage(OTO, modes.get(callbackData), chatId, 2);
            }

            // Обработка выбора для ПТО счетчика и концентратора
            case "ptoIIK", "ptoIVKE" -> {
                String textToSend;
                if ("ptoIIK".equals(callbackData)) {
                    textToSend = "Пожалуйста, загрузите фото счетчика и введите показания.";
                    processStates.put(chatId, ProcessState.WAITING_FOR_METER_PHOTO);
                } else {
                    textToSend = "Пожалуйста, загрузите фото концентратора и введите его номер.";
                    processStates.put(chatId, ProcessState.WAITING_FOR_DC_PHOTO);
                }
                sendMessage(chatId, textToSend);
            }

            case "otoIIK", "otoIVKE" -> {
                if (callbackData.equals("otoIIK")) {
                    sendTextMessage("Выберите вид ОТО ИИК: ", otoIIKButtons, chatId, 2);
                    processStates.put(chatId, ProcessState.IIK_WORKS);
                } else {
                    sendTextMessage("Выберите вид ОТО ИВКЭ: ", otoIVKEButtons, chatId, 2);
                    processStates.put(chatId, ProcessState.DC_WORKS);
                }
            }

            case "wkDrop", "setNot", "powerSupplyRestoring", "dcRestart" -> {
                switch (callbackData) {
                    case "wkDrop" -> {
                        sendMessage(chatId, "Введите номер прибора учета: ");
                        otoTypes.put(chatId, OtoType.WK_DROP);
                    }
                    case "dcRestart" -> {
                        sendMessage(chatId, "Введите номер концентратора: ");
                        otoTypes.put(chatId, OtoType.DC_RESTART);
                    }
                    case "setNot" -> {
                        String textToSend = processStates.get(chatId).equals(ProcessState.IIK_WORKS) ?
                                "Введите номер прибора учета: " : "Введите номер концентратора: ";
                        sendMessage(chatId, textToSend);
                        otoTypes.put(chatId, OtoType.SET_NOT);
                    }
                    default -> {
                        String textToSend = processStates.get(chatId).equals(ProcessState.IIK_WORKS) ?
                                "Введите номер прибора учета: " : "Введите номер концентратора: ";
                        sendMessage(chatId, textToSend);
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
                    processStates.put(chatId, ProcessState.WAITING_FOR_TT_PHOTO);
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoTypes.put(chatId, OtoType.TT_CHANGE);
            }

            case "dcChangeWithPhoto", "dcChangeWithOutPhoto" -> {
                String textToSend = "";
                otoTypes.put(chatId, OtoType.DC_CHANGE);
                if ("dcChangeWithPhoto".equals(callbackData)) {
                    processStates.put(chatId, ProcessState.WAITING_FOR_DC_PHOTO);
                    textToSend = "Загрузите фото демонтируемого концентратора и введите его номер: ";
                } else textToSend = "Введите номер демонтируемого концентратора: ";
                sendMessage(chatId, textToSend);
            }

            case "meterChangeWithPhoto", "meterChangeWithoutPhoto" -> {
                String textToSend = "";
                otoTypes.put(chatId, OtoType.METER_CHANGE);

                if ("meterChangeWithPhoto".equals(callbackData)) {
                    textToSend = "📸 Пожалуйста, загрузите фото **ДЕМОНТИРОВАННОГО** прибора и введите показания.";
                    processStates.put(chatId, ProcessState.WAITING_FOR_METER_PHOTO);
                } else textToSend = "Введите номер демонтируемого прибора учета: ";
                sendMessage(chatId, textToSend);
            }

            case "LOADING_COMPLETE" -> {
                if (isPTO) {
                    clearData();
                    sendMessage(chatId, "Для продолжения снова нажмите /start");
                } else {
                    sendTextMessage(actionConfirmation(chatId), confirmMenu, chatId, 2);
                }
            }

            case "confirm", "cancel" -> {
                String textToSend;
                if ("confirm".equals(callbackData)) {
                    textToSend = "Информация сохранена.";
                    sendMessage(chatId, "Подождите, идёт загрузка данных...");
                    sheetsFilling(chatId);
                } else {
                    textToSend = "Информация не сохранена.";
                }
                sendMessage(chatId, textToSend);
                clearData();
                sendMessage(chatId, "Для продолжения снова нажмите /start");
            }

            case "NOT", "lowPLC", "NOT3", "NOT2", "seasonNOT", "NOT1" -> {
                processInfo += Map.of(
                        "NOT", "НОТ. Потребитель отключен.",
                        "seasonNOT", "НОТ. Сезонный потребитель.",
                        "lowPLC", "НОТ. Низкий уровень PLC сигнала.",
                        "NOT3", "Прибор учета демонтирован (НОТ3).",
                        "NOT2", "Прибор учета сгорел (НОТ2).",
                        "NOT1", "Местонахождения ПУ неизвестно (НОТ1).").get(callbackData);
                formingOtoLog(processInfo, OtoType.SET_NOT);
                sendTextMessage("Введите номер следующего ПУ или закончите ввод.", CompleteButton, chatId, 1);
//                sendTextMessage(actionConfirmation(chatId), confirmMenu, chatId, 2);
            }
            case "meterMount", "dcMount" -> {
                String textToSend = "";
                if ("meterMount".equals(callbackData)) {
                    textToSend = " номер концентратора, к которому привязан ИИК (если номер не известен - введите \\\"0\\\"): \"";
                    processStates.put(chatId, ProcessState.IIK_MOUNT);
                } else {
                    textToSend = "наименование станции";
                    processStates.put(chatId, ProcessState.DC_MOUNT);
                }
                sendMessage(chatId, "Введите " + textToSend + ": ");
            }
            default -> sendMessage(chatId, "Неизвестное действие. Попробуйте еще раз.");
        }
    }

    private void concludeDeviceOperation(long chatId) {
        OtoType otoType = otoTypes.get(chatId);
        Object typeIndicator = otoType != null ? otoType : processStates.get(chatId);
        formingOtoLog(processInfo, typeIndicator);
        sendTextMessage(actionConfirmation(null), confirmMenu, chatId, 2);
    }

    private void clearData() {
        otoLog.clear();
        sequenceNumber = 0;
        processStates.clear();
        otoTypes.clear();
        processInfo = "";
        isPTO = false;
    }

    private void savePhoto(long chatId, PendingPhoto pending) {
        OtoType operationType = otoTypes.get(chatId);
        String deviceNumber = pending.getDeviceNumber();
        // Получаем состояние загрузки фото (если нет, создаем новое)
        PhotoState photoState = photoStates.computeIfAbsent(chatId, key -> new PhotoState(deviceNumber));
        if (isPTO || !PHOTO_SUBDIRS_NAME.containsKey(operationType)) { // Только фото ПТО
            handleUncontrolledPhoto(chatId, pending);
            return;
        }
        handleChangingEquipmentPhoto(chatId, pending, operationType, photoState);
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
    private void handleChangingEquipmentPhoto(long chatId, PendingPhoto pending, OtoType operationType, PhotoState photoState) {
        // Определяем, необходимость загрузки нового фото
        String photoPhase = photoState.getNextPhotoType(operationType);
        if (photoPhase == null) {
            sendMessage(chatId, "⚠ Ошибка: уже загружены все необходимые фото.");
            return;
        }
        // Сохранение фото
        doSave(chatId, pending);
        photoState.markPhotoUploaded(photoPhase);

        addChangingInfo(pending);
        pendingPhotos.remove(chatId);

        // Проверка необходимости продолжения загрузки фото
        if (photoState.isComplete(operationType)) {
            sendMessage(chatId, "✅ Все фото загружены!");
            changeReasonInput(chatId, operationType);

            photoStates.remove(chatId);
        } else {
            // Рекомендации по загрузке фото
            sendNextPhotoInstruction(chatId, photoState.getNextPhotoType(operationType));
            setProcessState(operationType, chatId);
        }
    }

    private void changeReasonInput(long chatId, OtoType operationType) {
        sendMessage(chatId, "Введите причину замены: ");
        sequenceNumber = replacedEquipmentDatum.get(operationType).size();
        processStates.clear();
    }

    private void setProcessState(OtoType operationType, long chatId) {
        switch (operationType) {
            case METER_CHANGE -> processStates.put(chatId, ProcessState.WAITING_FOR_METER_PHOTO);
            case TT_CHANGE -> processStates.put(chatId, ProcessState.WAITING_FOR_TT_PHOTO);
            default -> {
            }
        }
    }


    private void doSave(long chatId, PendingPhoto pending) {
//        OtoType operationType = otoTypes.get(chatId);
        try {
            Path userDir = Paths.get(createSavingPath(pending, chatId));

            Files.createDirectories(userDir);

            String newFileName = createNewFileName(pending, chatId);
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
            case "установлен" -> "📸 Пожалуйста, загрузите фото **УСТАНОВЛЕННОГО** прибора и введите "
                    + (processStates.get(chatId).equals(ProcessState.IIK_WORKS) ? "показания" : "его номер");
            case "ф.A" -> "📸 Прикрепите фото **ТТ фазы A** и введите его номер:";
            case "ф.B" -> "📸 Прикрепите фото **ТТ фазы B** и введите его номер:";
            case "ф.C" -> "📸 Прикрепите фото **ТТ фазы C** и введите его номер:";
            default -> null;
        };

        if (message != null) {
            sendMessage(chatId, message);
        }
    }

    private String createSavingPath(PendingPhoto pending, long chatId) {

        OtoType operationType = otoTypes.get(chatId);
        String baseDir = PHOTO_PATH + File.separator;
        String path = savingPaths.getOrDefault(pending.getDeviceNumber(), "unknown");

        if (operationType != null) {
            if (PHOTO_SUBDIRS_NAME.containsKey(operationType)) {
                baseDir += PHOTO_SUBDIRS_NAME.get(operationType) + File.separator;
            }
        } else if (!processStates.get(chatId).equals(ProcessState.WAITING_FOR_DC_PHOTO))
            path = path.substring(0, path.lastIndexOf("\\"));

        if (photoStates.get(chatId).getUploadedPhotos().isEmpty()) chgePath = path;
        else if (photoStates.get(chatId).getUploadedPhotos().size() < 2) path = chgePath;
        return baseDir + path;
    }

    private String createNewFileName(PendingPhoto pending, Long chatId) {
        OtoType operationType = otoTypes.get(chatId);
        String photoSuffix = "";
        String additionalInfo = pending.getAdditionalInfo() != null ? "_" + pending.getAdditionalInfo() : "";
        if (operationType != null) {
            PhotoState photoState = photoStates.get(chatId);
            photoSuffix = getSavingPhotoSuffix(operationType, photoState);
            if (operationType == OtoType.TT_CHANGE) {
                additionalInfo = "_(" + photoState.getNextPhotoType(operationType) + additionalInfo.replace("_", ", №") + ")";
            } else {
                additionalInfo = "";
            }
        }
        return formattedCurrentDate + "_" + getSavingPhotoPrefix(pending.getType()) + pending.getDeviceNumber() +
                additionalInfo + photoSuffix + ".jpg";
    }

    private String getSavingPhotoPrefix(String type) {
        return switch (type) {
            case "counter" -> "ИИК_";
            case "concentrator" -> "ИВКЭ_";
            case "tt" -> "ТТ_";
            default -> "unknown_";
        };
    }

    private String getSavingPhotoSuffix(OtoType operationType, PhotoState state) {
        if (operationType != null && (operationType.equals(OtoType.METER_CHANGE)
                || operationType.equals(OtoType.DC_CHANGE))) {
            return "_" + state.getNextPhotoType(operationType);
        } else return "";
    }


    private void addChangingInfo(PendingPhoto pending) {
        if (pending.getType().equals("counter")) {
            processInfo += pending.getDeviceNumber() + "_" + pending.getAdditionalInfo() + "_";
        } else
            processInfo += pending.getType().equals("concentrator") ? pending.getDeviceNumber() + "_" : pending.getAdditionalInfo() + "_";
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

    private void sheetsFilling(long chatId) {
        if (otoLog.isEmpty()) return;
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             Workbook operationLog = new XSSFWorkbook(new FileInputStream(OPERATION_LOG_PATH));
             FileOutputStream fileOut = new FileOutputStream(OPERATION_LOG_PATH);
             FileOutputStream fileOtoOut = new FileOutputStream(PLAN_OTO_PATH);
        ) {

            boolean isDcWorks = containsDcWorks();
            boolean isDcChange = containsDcChange();

            Sheet meterWorkSheet = planOTOWorkbook.getSheet("ИИК");
            Sheet operationLogSheet = operationLog.getSheet("ОЖ");

            String taskOrder = dataPreparing(operationLogSheet, meterWorkSheet, isDcWorks);


//            if (isMounting && !isDcLocation) {
//                for (Map.Entry<String, String> entry : otoLog.entrySet()) {
//                    String[] entryParts = entry.getValue().split("_");
//                    String station = entryParts[2];
//                    String substation = entryParts[3];
//                    Optional<String> keyOpt = savingPaths.entrySet().stream()
//                            .filter(e -> e.getValue().contains(station) && e.getValue().contains(substation))
//                            .map(Map.Entry::getKey)
//                            .findAny()
//                            .or(() -> savingPaths.entrySet().stream()
//                                    .filter(e -> e.getValue().contains(station))
//                                    .map(Map.Entry::getKey)
//                                    .findAny()
//                            );
//
//                    keyOpt.ifPresent(key -> {
//                        // Используй найденный key
//                        System.out.println("Найден ключ: " + key);
//                    });
//                }
//            }

            if (isDcWorks) {
                fillDcSection(planOTOWorkbook.getSheet("ИВКЭ"), taskOrder, isDcChange);
            }
            operationLog.write(fileOut);
            planOTOWorkbook.write(fileOtoOut);
            otoLog.clear();

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
    }

    private String dataPreparing(Sheet operationLogSheet, Sheet meterSheet, boolean isDcWorks) {
        int orderColumnNumber = excelFileService.findColumnIndex(meterSheet, "Отчет бригады о выполнении ОТО");
        int deviceNumberColumnIndex = excelFileService.findColumnIndex(meterSheet, isDcWorks ? "Номер УСПД" : "Номер счетчика");
        int operationLogLastRowNumber = operationLogSheet.getLastRowNum();
        int addedRows = 0;
        boolean isLogFilled = false;
        boolean isMounting = containsMountWork();
        String taskOrder = "";

        List<Row> meterRows = new ArrayList<>();
        for (Row row : meterSheet) {
            meterRows.add(row);
        }

        for (Row otoRow : meterRows) {
//            log.info(String.valueOf(otoRow.getRowNum()));
            String deviceNumber = excelFileService.getCellStringValue(otoRow.getCell(deviceNumberColumnIndex));
            String logData = otoLog.getOrDefault(deviceNumber, "");
            boolean dataContainsNot123 = logData.contains("НОТ1") || logData.contains("НОТ2") || logData.contains("НОТ3");

            if (!logData.isEmpty()) {
                if (!isLogFilled) {
                    Row newRow = operationLogSheet.createRow(operationLogLastRowNumber + ++addedRows);
                    if (isDcWorks) {
                        excelFileService.clearCellData(getIndexesOfCleaningCells(dcColumnsToClear, meterSheet), newRow); //удаление данных из ненужных ячеек
                    }
                    if (isMounting) {
                        int meterSheetLastRowNumber = meterSheet.getLastRowNum();
                        Row newOtoRow = meterSheet.createRow(meterSheetLastRowNumber + 1);
                        excelFileService.copyRow(otoRow, newOtoRow, orderColumnNumber);
                        excelFileService.clearCellData(getIndexesOfCleaningCells(meterMountColumnsToClear, meterSheet), newOtoRow);
                        taskOrder = addOtoData(deviceNumber, logData, newRow, newOtoRow, deviceNumberColumnIndex, dataContainsNot123, orderColumnNumber);
                    } else {
                        excelFileService.copyRow(otoRow, newRow, orderColumnNumber);
                        taskOrder = addOtoData(deviceNumber, logData, newRow, otoRow, deviceNumberColumnIndex, dataContainsNot123, orderColumnNumber);
                    }
                    isLogFilled = true;
                }
                if (dataContainsNot123) {
                    excelFileService.clearCellData(getIndexesOfCleaningCells(not123ColumnsToClear, meterSheet), otoRow);
                    String notType = taskOrder.substring(taskOrder.indexOf("(") + 1, taskOrder.indexOf("(") + 1 + 4);
                    otoRow.getCell(excelFileService.findColumnIndex(meterSheet, "Текущее состояние")).setCellValue(notType);
                }
            }
        }
        addedRows = 0;
        return taskOrder;
    }

    private void prepareMountedDeviceRow(Row newOtoRow, String logData) {
        String[] dataParts = logData.split("_");

    }


    private void fillDcSection(Sheet dcWorkSheet, String taskOrder, boolean isDcChange) { // заполнение данных на вкладке "ИВКЭ"
        int dcNumberColIndex = excelFileService.findColumnIndex(dcWorkSheet, "Серийный номер концентратора");
        int dcCurrentStateColIndex = excelFileService.findColumnIndex(dcWorkSheet, "Состояние ИВКЭ");

        for (Row row : dcWorkSheet) {
            String deviceNumber = excelFileService.getCellStringValue(row.getCell(dcNumberColIndex));
            String logData = otoLog.getOrDefault(deviceNumber, "");
            if (!logData.isEmpty()) {
                row.createCell(dcCurrentStateColIndex).setCellValue(taskOrder);
                if (isDcChange) row.getCell(dcNumberColIndex).setCellValue(logData.split("_")[1]);
            }
        }
    }


    private boolean containsDcWorks() {
        return Stream.concat(otoLog.keySet().stream(), otoLog.values().stream())
                .anyMatch(val -> val.contains("LW") || val.contains("LJ"));
    }

    private boolean containsDcChange() {
        return otoLog.values().stream().anyMatch(val -> val.contains("dcChange"));
    }

    private boolean containsMountWork() {
        return otoLog.values().stream().anyMatch(v -> v.contains("Mount"));
    }


    private int[] getIndexesOfCleaningCells(String[] columnNames, Sheet sheet) {
        return Arrays.stream(columnNames)
                .mapToInt(name -> excelFileService.findColumnIndex(sheet, name))
                .filter(index -> index >= 0)
                .toArray();
    }


    private String addOtoData(String deviceNumber, String logData, Row newLogRow, Row otoRow, int deviceNumberColumnIndex, boolean dataContainsNot123, int orderColumnNumber) {
        String workType = logData.substring(0, logData.indexOf("_"));
        String[] dataParts = logData.split("_");
        List<String> columns = getStrings(workType);

        String taskOrder = straightFormattedCurrentDate + " - " + columns.get(2) + switch (workType) {

            case "WK", "NOT", "meterSupply", "dcSupply", "dcRestart" -> {
                if (dataParts.length > 1) {
                    if (!dataContainsNot123) yield " " + dataParts[1];
                    else {
                        int firstSpace = dataParts[1].indexOf(" ");
                        int secondSpace = dataParts[1].indexOf(" ", firstSpace + 1);
                        yield dataParts[1].substring(0, secondSpace) + " № " + deviceNumber + dataParts[1].substring(secondSpace);
                    }
                } else yield "";
            }

            case "meterChange" -> {
                Object mountingDeviceNumber = parseMeterNumber(dataParts[2]);
                // Внесение номера Устройства в журнал "Контроль ПУ РРЭ"
                if (mountingDeviceNumber instanceof Long) {
                    otoRow.getCell(deviceNumberColumnIndex).setCellValue((Long) mountingDeviceNumber);
                } else {
                    otoRow.getCell(deviceNumberColumnIndex).setCellValue((String) mountingDeviceNumber);
                }
                yield deviceNumber + " (" + dataParts[1]
                        + " кВт) на " + dataParts[2] + " (" + dataParts[3] + " кВт). Причина замены: " + dataParts[4] + ".";
            }
            case "ttChange" ->
                    String.format("%s, номиналом %s, с классом точности %s, %sг.в. №АВС = %s, %s, %s. Причина замены: %s.",
                            dataParts[1], dataParts[2], dataParts[3], dataParts[4],
                            dataParts[5], dataParts[6], dataParts[7], dataParts[8]);
            case "dcChange" -> {
                otoRow.getCell(deviceNumberColumnIndex).setCellValue(dataParts[1]);
                yield String.format("%s на концентратор № %s. Причина замены: %s.", deviceNumber, dataParts[1], dataParts[2]);
            }
            case "meterMount" -> {
                Object mountingDeviceNumber = parseMeterNumber(dataParts[3]); //Номер счетчика
                if (mountingDeviceNumber instanceof Long) {
                    otoRow.getCell(13).setCellValue((Long) mountingDeviceNumber);
                } else {
                    otoRow.getCell(13).setCellValue((String) mountingDeviceNumber);
                }
                otoRow.getCell(9).setCellValue(dataParts[5]); //4 Точка учёта
                otoRow.getCell(10).setCellValue(dataParts[7]); // Место установки счетчика (Размещение счетчика)
                otoRow.getCell(11).setCellValue(dataParts[6]); // Адрес установки
                otoRow.getCell(12).setCellValue(dataParts[4].toUpperCase()); // Марка счётчика
                Object mountDeviceNumber = parseMeterNumber(dataParts[3]);
                if (mountDeviceNumber instanceof Long) {
                    otoRow.getCell(deviceNumberColumnIndex).setCellValue((Long) mountDeviceNumber);
                } else {
                    otoRow.getCell(deviceNumberColumnIndex).setCellValue((String) mountDeviceNumber);
                }

//                otoRow.getCell(13).setCellValue(dataParts[3]); // Номер счетчика
                otoRow.getCell(14).setCellValue(deviceNumber); // Номер УСПД
                Cell mountDate = otoRow.getCell(15);
                mountDate.setCellValue(dataParts[10]); // Дата монтажа ТУ
                excelFileService.setDateCellStyle(mountDate);
//                otoRow.getCell(15).setCellValue(dataParts[10]); // Дата монтажа ТУ
                otoRow.getCell(16).setCellValue("НОТ"); // Текущее состояние
                excelFileService.copyRow(otoRow, newLogRow, orderColumnNumber);
                yield "";
            }
            default -> null;
        };
        Cell date = newLogRow.getCell(16);
        excelFileService.setDateCellStyle(date);
        newLogRow.getCell(17).setCellValue(columns.get(0));
        newLogRow.getCell(18).setCellValue(columns.get(1));
        newLogRow.getCell(19).setCellValue("");
        newLogRow.getCell(20).setCellValue("Исполнитель"); //TODO: взять исполнителя из БД по chatId
        newLogRow.getCell(21).setCellValue(taskOrder);
        otoRow.getCell(orderColumnNumber).setCellValue(taskOrder);
        return taskOrder;

//      newLogRow.createCell(22).setCellValue("Выполнено");   //TODO: добавить после реализации внесения корректировок в Горизонт либо БД
    }

    private static List<String> getStrings(String data) {
        return fillingData.get(data);
    }

    private String actionConfirmation(Long chatId) {
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
                        "%s, номиналом %s, с классом точности %s, %sг.в. №АВС = %s, %s, %s. Причина: %s.",
                        str[1], str[2], str[3], str[4], str[5], str[6], str[7], str[8]));
                case "dcChange" -> resultStr.append(String.format(
                        "%s на концентратор №%s. Причина: %s.", key, str[1], str[2]));
                case "meterMount" -> resultStr.append(String.format(
                        "\nНаименование ТУ: %s, \nПрибор учёта №: %s. \nСтанция: %s, \nТП/КТП: %s, \nАдрес: %s, \nДата монтажа: %s.",
                        str[5], str[3], str[1], str[2], str[6], str[10]));
                default -> {
                    String device = ProcessState.IIK_WORKS.equals(processStates.get(chatId)) ? " ПУ" : " Концентратор";
                    resultStr.append(String.format(device + " № %s - ", key));
                    if (str.length > 1) resultStr.append(" ").append(str[str.length - 1]).append(".");
                }
            }
            resultStr.append("\n");
        }
        return resultStr.toString();
    }

    private void formingOtoLog(String deviceInfo, Object typeIndicator) {
        String deviceNumber = deviceInfo.substring(0, deviceInfo.indexOf("_"));
        String workType = resolveWorkType(typeIndicator, deviceNumber);
        otoLog.put(deviceNumber, workType + deviceInfo.substring(deviceInfo.indexOf("_")));
        processInfo = "";
        sequenceNumber = 0;
    }

    private String resolveWorkType(Object typeIndicator, String deviceNumber) {
        if (typeIndicator instanceof OtoType otoType) {
            return switch (otoType) {
                case METER_CHANGE -> "meterChange";
                case TT_CHANGE -> "ttChange";
                case DC_CHANGE -> "dcChange";
                case SET_NOT -> "NOT";
                case SUPPLY_RESTORING ->
                        deviceNumber.contains("LW") || deviceNumber.contains("LJ") ? "dcSupply" : "meterSupply";
                default -> "unknown";
            };
        } else if (typeIndicator instanceof ProcessState processState) {
            return switch (processState) {
                case IIK_MOUNT -> "meterMount";
                case DC_MOUNT -> "dcMount";
                default -> "unknown";
            };
        }
        return "unknown";
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
