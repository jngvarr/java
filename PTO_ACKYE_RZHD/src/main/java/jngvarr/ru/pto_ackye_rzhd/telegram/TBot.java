package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.config.BotConfig;
import jngvarr.ru.pto_ackye_rzhd.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.entities.Substation;
import jngvarr.ru.pto_ackye_rzhd.entities.User;
import jngvarr.ru.pto_ackye_rzhd.services.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
import java.time.LocalDate;
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
    private final MeteringPointService meteringPointService;
    private final MeterService meterService;
    private final SubstationService substationService;
    private final PtoService ptoService;
    private UserServiceImpl userService;
    static final String ERROR_TEXT = "Error occurred: ";
    private Map<Long, Integer> sendMessagesIds = new HashMap<>();
    private List<Message> sentMessages = new ArrayList<>();
    private final ExcelFileService excelFileService;
    private final PreparingPhotoService preparingPhotoService;
    // Карта для хранения информации о фото, ожидающих подтверждения
    private Map<Long, PendingPhoto> pendingPhotos = new HashMap<>();
    private Map<String, String> savingPaths;
    private static final long ADMIN_CHAT_ID = 199867696L;

    public enum ProcessState {
        WAITING_FOR_METER_PHOTO,
        WAITING_FOR_DC_PHOTO,
        WAITING_FOR_TT_PHOTO,
        MANUAL_INSERT_METER_NUMBER,
        MANUAL_INSERT_METER_INDICATION,
        IIK_WORKS,
        DC_WORKS,
        IIK_MOUNT,
        DC_MOUNT,
        REGISTRATION
    }

    private void handleStartCommand(long chatId, long userId) {
        sendTextMessage(MAIN_MENU, startMenuButtons, chatId, userId, 1);
    }

    // Мапа для хранения состояния диалога по userId
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
//    private boolean isMeterInstalled;


    public TBot(BotConfig config, TBotService tBotService, MeteringPointService meteringPointService, MeterService meterService, SubstationService substationService, PtoService ptoService, UserServiceImpl userService, ExcelFileService excelFileService, PreparingPhotoService preparingPhotoService) throws TelegramApiException {
        super(config.getBotToken());
        this.config = config;
        this.tBotService = tBotService;
        this.meteringPointService = meteringPointService;
        this.meterService = meterService;
        this.substationService = substationService;
        this.ptoService = ptoService;
        this.userService = userService;
        this.excelFileService = excelFileService;
        this.preparingPhotoService = preparingPhotoService;
        savingPaths = excelFileService.getPhotoSavingPathFromExcel();//TODO подумать

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать работу"));
        listOfCommands.add(new BotCommand("/help", "немного информации по использованию бота"));
        listOfCommands.add(new BotCommand("/stop", "сбросить всё, начать заново"));
        listOfCommands.add(new BotCommand("/register", "регистрация нового пользователя"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot`s command list" + e.getMessage());
        }
        execute(new SendMessage("199867696", INTRO));
    }

    @Override
    public void onUpdateReceived(Update update) {
        long userId;
        long chatId;
        if (update.hasMessage()) {
            forwardMessage(update.getMessage());
            userId = update.getMessage().getFrom().getId();
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
        } else {
            return;
        }
        User user = userService.getUserById(userId);
        String incomingText = update.hasMessage() && update.getMessage().hasText()
                ? update.getMessage().getText()
                : "";

        if (user == null && "/register".equals(incomingText)) {
            registerUser(update);
            sendMessage(chatId, userId, "Пользователь успешно зарегистрирован.");
            return;
        } else if ("/register".equals(incomingText)) {
            sendMessage(chatId, userId, "Вы уже зарегистрированы!!!");
        }

        if (user == null || !user.isAccepted()) {
            sendMessage(chatId, userId, "Пожалуйста, пройдите регистрацию и дождитесь валидации администратора.");
            return;
        }

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

    private void forwardMessage(Message userMessage) {
        ForwardMessage forward = new ForwardMessage();
        forward.setChatId(String.valueOf(ADMIN_CHAT_ID)); // куда
        forward.setFromChatId(String.valueOf(userMessage.getChatId())); // откуда
        forward.setMessageId(userMessage.getMessageId()); // какое сообщение

        try {
            execute(forward);
        } catch (TelegramApiException e) {
            log.error("Не удалось переслать сообщение админу: " + e.getMessage());
        }
    }

    private void handlePhotoMessage(Update update) {
        long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();

        // Проверяем, есть ли подпись к фото
        String manualInput = update.getMessage().getCaption();

        // Если фото не запрашивалось
        if (!processStates.containsKey(userId)) {
            sendMessage(chatId, userId, "Фото не запрашивалось. Если хотите начать, нажмите /start");
            return;
        }
        sendMessage(chatId, userId, "Подождите, идёт обработка фото....");
        ProcessState currentState = processStates.get(userId);
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
                sendMessage(chatId, userId, "Не удалось обработать изображение.");
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
            pendingPhotos.put(userId, pendingPhoto);
            if (type.equals("counter")) {
                if (manualInput != null) pendingPhoto.setAdditionalInfo(manualInput.trim());

                // 7. Если штрихкод найден и есть показания – сразу сохраняем
                if (barcodeText != null && pendingPhoto.getAdditionalInfo() != null) {
                    savePhoto(userId, chatId, pendingPhoto);
                    return;
                }
                if (barcodeText == null) {
                    sendMessage(chatId, userId, "Штрихкод не найден. Введите номер ПУ вручную:");
                    processStates.put(userId, ProcessState.MANUAL_INSERT_METER_NUMBER);
                    return;
                }
                if (manualInput == null) {
                    sendMessage(chatId, userId, "Показания счетчика не введены. Введите показания счётчика:");
                    processStates.put(userId, ProcessState.MANUAL_INSERT_METER_INDICATION);
                }

            } else if (type.equals("tt")) {
                if (manualInput != null) {
                    pendingPhoto.setAdditionalInfo(manualInput);
                    savePhoto(userId, chatId, pendingPhoto);
                } else {
                    PhotoState photoState = photoStates.get(userId);
                    OtoType otoType = otoTypes.get(userId);
                    sendMessage(chatId, userId, "❌ Не указан номер трансформатора тока!! Повторите предыдущее действие!");
                    sendNextPhotoInstruction(userId, chatId, photoState.getNextPhotoType(otoType));
                }
            } else {
                if (manualInput != null) {
                    pendingPhoto.setDeviceNumber(manualInput.trim());
                    savePhoto(userId, chatId, pendingPhoto);
                } else {
                    pendingPhoto.setAdditionalInfo("Данные не требуются.");
                    PhotoState photoState = photoStates.get(userId);
                    OtoType otoType = otoTypes.get(userId);
                    sendMessage(chatId, userId, "❌ Номер концентратора не обнаружен!! Пожалуйста введите еще раз:");
                    processStates.put(userId, ProcessState.MANUAL_INSERT_METER_NUMBER);
//                    sendNextPhotoInstruction(userId, photoState.getNextPhotoType(otoType));
                }
            }
        } catch (Exception e) {
            log.error("Ошибка обработки фото: " + e.getMessage());
            sendMessage(chatId, userId, "Произошла ошибка при обработке фото.");
        }
    }


    private void handleTextMessage(Update update) {
        String msgText = update.getMessage().getText();
        long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();
        ProcessState processState = processStates.get(userId);
        OtoType otoType = otoTypes.get(userId);
        switch (msgText) {
            case "/start" -> {
                handleStartCommand(chatId, userId);
                clearData();
                return;
            }
            case "/help" -> {
                sendMessage(chatId, userId, HELP);
                return;
            }
//            case "/register" -> {
//                processStates.put(userId, ProcessState.REGISTRATION);
//                registerUser(update);
//                clearData();
//                return;
//            }
            case "/stop" -> {
                sendMessage(chatId, userId, "Работа прервана, для продолжения нажмите /start");
                clearData();
                return;
            }
//            case "/accept" -> {
//
//                sendMessage(chatId, "Работа прервана, для продолжения нажмите /start");
//                clearData();
//                return;
//            }
        }

        if (processState != null) {
            switch (processState) {
                case MANUAL_INSERT_METER_NUMBER, MANUAL_INSERT_METER_INDICATION -> {
                    handleManualInsert(userId, chatId, msgText);
                    return;
                }
                case IIK_MOUNT, DC_MOUNT -> {
                    handleEquipmentMount(userId, chatId, msgText, processState);
                    return;
                }
            }
        }
        if (otoType != null) {
            switch (otoType) {
                case TT_CHANGE, METER_CHANGE, DC_CHANGE -> {
                    handleEquipmentChange(userId, chatId, msgText, otoType);
                    return;
                }
                case WK_DROP, SET_NOT, SUPPLY_RESTORING, DC_RESTART -> {
                    handleOtherOtoTypes(userId, chatId, msgText);
                    return;
                }
            }
        }
        sendMessage(chatId, userId, "Команда не распознана. Попробуйте еще раз.");

    }

    private void handleOtherOtoTypes(long userId, long chatId, String msgText) {
        OtoType currentOtoType = otoTypes.get(userId);
        String messageText = msgText.trim();

        switch (currentOtoType) {
            case WK_DROP -> {
                otoLog.put(messageText, "WK_");
                editTextAndButtons("Введите номер следующего прибора учета или закончите ввод.", CompleteButton, chatId, userId, 1);
            }
            case SET_NOT -> {
                processInfo += msgText + "_";
                if (sequenceNumber == 0) {
                    if (ProcessState.DC_WORKS.equals(processStates.get(userId))) {
                        sendMessage(chatId, userId, "Введите причину отключения: ");
                    } else {
                        chooseNotType(chatId, userId);
                    }
                    sequenceNumber++;
                } else {
                    formingOtoLog(processInfo, currentOtoType);
                    sendTextMessage(actionConfirmation(userId), confirmMenu, chatId, userId, 2);
                }
            }
            case SUPPLY_RESTORING, DC_RESTART -> {
                if (currentOtoType.equals(OtoType.SUPPLY_RESTORING)) {
                    processInfo += msgText + "_";
                    if (sequenceNumber == 0) {
                        sendMessage(chatId, userId, "Опишите причину неисправности: ");
                        sequenceNumber++;
                    } else {
                        formingOtoLog(processInfo, currentOtoType);
                        sendTextMessage(actionConfirmation(userId), confirmMenu, chatId, userId, 2);
                    }
                } else {
                    otoLog.put(messageText, "dcRestart_");
                    sendTextMessage(actionConfirmation(userId), confirmMenu, chatId, userId, 2);
                }
            }
        }
    }

    private void chooseNotType(Long chatId, Long userId) {
        sendTextMessage("Выберите причину отключения: ",
                Map.of(
                        "Потребитель отключен.", "NOT",
                        "Сезонный потребитель.", "seasonNOT",
                        "Низкий уровень PLC сигнала", "lowPLC",
                        "Прибор учета демонтирован (НОТ3)", "NOT3",
                        "Прибор учета сгорел (НОТ2)", "NOT2",
                        "Местонахождения ПУ неизвестно (НОТ1 украден?)", "NOT1"),
                chatId, userId, 1);
    }

    private void handleEquipmentChange(long userId, long chatId, String msgText, OtoType otoType) {
        Map<Integer, String> replacedEquipmentData = replacedEquipmentDatum.get(otoType);
        if (msgText != null && !msgText.trim().isEmpty()) {
            processInfo += msgText + "_";
        }
        if (sequenceNumber < replacedEquipmentData.size()) {
            if (processStates.get(userId).equals(ProcessState.WAITING_FOR_TT_PHOTO) && sequenceNumber == 4) {
                sendMessage(chatId, userId, "📸 Прикрепите фото **ТТ фазы A** и введите его номер:");
            } else {
                sendMessage(chatId, userId, replacedEquipmentData.get(sequenceNumber));
            }
            sequenceNumber++;
        } else concludeDeviceOperation(userId, chatId);
    }

    private void handleEquipmentMount(long userId, long chatId, String msgText, ProcessState state) {
        Map<Integer, String> mountedEquipmentData = mountedEquipmentDatum.get(state);
        if (msgText != null && !msgText.trim().isEmpty()) {
            processInfo += msgText + "_";
        }
        if (ProcessState.IIK_MOUNT.equals(processStates.get(userId)) && sequenceNumber == 0) {
            String path = savingPaths.get(msgText);
            if (path != null) {
                sequenceNumber += 2;
                addPathToProcessInfo(path);
            }
        }
        if (sequenceNumber < mountedEquipmentData.size()) {
            sendMessage(chatId, userId, mountedEquipmentData.get(sequenceNumber));
            sequenceNumber++;
        } else concludeDeviceOperation(userId, chatId);
    }

    private void addPathToProcessInfo(String path) {
        String[] pathParts = path.split("\\\\");
        processInfo = processInfo + pathParts[pathParts.length - 2] + "_" + pathParts[pathParts.length - 1] + "_";
        isDcLocation = true;
    }

    private void handleManualInsert(long userId, long chatId, String deviceNumber) {
        String manualInput = deviceNumber.trim();
        PendingPhoto pending = pendingPhotos.get(userId);
        if (pending != null) {
            if (processStates.get(userId).equals(ProcessState.MANUAL_INSERT_METER_INDICATION)) {
                pending.setAdditionalInfo(manualInput);
            } else {
                pending.setDeviceNumber(manualInput);
            }
            boolean isDataFull = pending.getDeviceNumber() != null && pending.getAdditionalInfo() != null;
            if (isDataFull) {
                savePhoto(userId, chatId, pending);
            } else if (pending.getDeviceNumber() == null) {
                sendMessage(chatId, userId, "Заводской номер не найден. Введите номер вручную:");
                processStates.put(userId, ProcessState.MANUAL_INSERT_METER_NUMBER);
            } else {
                sendMessage(chatId, userId, "Показания счетчика не введены. Введите показания счётчика:");
                processStates.put(userId, ProcessState.MANUAL_INSERT_METER_INDICATION);
            }
        } else {
            sendMessage(chatId, userId, "Ошибка: нет ожидающих фото для привязки показаний.");
        }
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long userId = update.getCallbackQuery().getFrom().getId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (callbackData) {
            case "mount" -> {
                editTextAndButtons(NEW_TU, modes.get(callbackData), chatId, userId, 1);
//                sendTextMessage(NEW_TU, modes.get(callbackData), chatId, 1);
            }
            case "pto" -> {
                isPTO = true;
                editTextAndButtons(PTO, modes.get(callbackData), chatId, userId, 2);
//                sendTextMessage(PTO, modes.get(callbackData), chatId, 2);
            }
            case "oto" -> {
//                sendTextMessage(OTO, modes.get(callbackData), chatId, 2);
                editTextAndButtons(OTO, modes.get(callbackData), chatId, userId, 2);
            }

            // Обработка выбора для ПТО счетчика и концентратора
            case "ptoIIK", "ptoIVKE" -> {
                String textToSend;
                if ("ptoIIK".equals(callbackData)) {
                    textToSend = "Пожалуйста, загрузите фото счетчика и введите показания.";
                    processStates.put(userId, ProcessState.WAITING_FOR_METER_PHOTO);
                } else {
                    textToSend = "Пожалуйста, загрузите фото концентратора и введите его номер.";
                    processStates.put(userId, ProcessState.WAITING_FOR_DC_PHOTO);
                }
                editMessage(chatId, userId, textToSend);
            }

            case "otoIIK", "otoIVKE" -> {
                if (callbackData.equals("otoIIK")) {
                    editTextAndButtons("Выберите вид ОТО ИИК: ", otoIIKButtons, chatId, userId, 2);
                    processStates.put(userId, ProcessState.IIK_WORKS);
                } else {
                    editTextAndButtons("Выберите вид ОТО ИВКЭ: ", otoIVKEButtons, chatId, userId, 2);
                    processStates.put(userId, ProcessState.DC_WORKS);
                }
            }

            case "wkDrop", "setNot", "powerSupplyRestoring", "dcRestart" -> {
                switch (callbackData) {
                    case "wkDrop" -> {
                        editMessage(chatId, userId, "Введите номер прибора учета: ");
                        otoTypes.put(userId, OtoType.WK_DROP);
                    }
                    case "dcRestart" -> {
                        editMessage(chatId, userId, "Введите номер концентратора: ");
                        otoTypes.put(userId, OtoType.DC_RESTART);
                    }
                    case "setNot" -> {
                        String textToSend = processStates.get(userId).equals(ProcessState.IIK_WORKS) ?
                                "Введите номер прибора учета: " : "Введите номер концентратора: ";
                        editMessage(chatId, userId, textToSend);
                        otoTypes.put(userId, OtoType.SET_NOT);
                    }
                    default -> {
                        String textToSend = processStates.get(userId).equals(ProcessState.IIK_WORKS) ?
                                "Введите номер прибора учета: " : "Введите номер концентратора: ";
                        editMessage(chatId, userId, textToSend);
                        otoTypes.put(userId, OtoType.SUPPLY_RESTORING);
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
                editTextAndButtons("Вид передачи данных: ",
                        Map.of("С приложением фото.", value1,
                                "Без приложения фото.", value2), chatId, userId, 2);
            }

            case "ttChangeWithPhoto", "ttChangeWithOutPhoto" -> {
                if ("ttChangeWithPhoto".equals(callbackData))
                    processStates.put(userId, ProcessState.WAITING_FOR_TT_PHOTO);
                editMessage(chatId, userId, "Введите номер прибора учета: ");
                otoTypes.put(userId, OtoType.TT_CHANGE);
            }

            case "dcChangeWithPhoto", "dcChangeWithOutPhoto" -> {
                String textToSend = "";
                otoTypes.put(userId, OtoType.DC_CHANGE);
                if ("dcChangeWithPhoto".equals(callbackData)) {
                    processStates.put(userId, ProcessState.WAITING_FOR_DC_PHOTO);
                    textToSend = "Загрузите фото демонтируемого концентратора и введите его номер: ";
                } else textToSend = "Введите номер демонтируемого концентратора: ";
                editMessage(chatId, userId, textToSend);
            }

            case "meterChangeWithPhoto", "meterChangeWithoutPhoto" -> {
                String textToSend = "";
                otoTypes.put(userId, OtoType.METER_CHANGE);

                if ("meterChangeWithPhoto".equals(callbackData)) {
                    textToSend = "📸 Пожалуйста, загрузите фото **ДЕМОНТИРОВАННОГО** прибора и введите показания.";
                    processStates.put(userId, ProcessState.WAITING_FOR_METER_PHOTO);
                } else textToSend = "Введите номер демонтируемого прибора учета: ";
                editMessage(chatId, userId, textToSend);
            }

            case "LOADING_COMPLETE" -> {
                if (isPTO) {
                    clearData();
                    sendMessage(chatId, userId, "Для продолжения снова нажмите /start");
                } else {
                    editTextAndButtons(actionConfirmation(userId), confirmMenu, chatId, userId, 2);
                }
            }

            case "confirm", "cancel" -> {
                String textToSend;
                if ("confirm".equals(callbackData)) {
                    textToSend = "Информация сохранена.";
                    sendMessage(chatId, userId, "Подождите, идёт загрузка данных...");
                    sheetsFilling(userId);
                } else {
                    textToSend = "Информация не сохранена.";
                }
                editMessage(chatId, userId, textToSend);
                clearData();
                sendMessage(chatId, userId, "Для продолжения снова нажмите /start");
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
                editTextAndButtons("Введите номер следующего ПУ или закончите ввод.", CompleteButton, chatId, userId, 1);
            }
            case "iikMount", "dcMount" -> {
                String textToSend = "";
                if ("iikMount".equals(callbackData)) {
                    textToSend = " номер концентратора, к которому привязан ИИК (если номер не известен - введите \\\"0\\\"): \"";
                    processStates.put(userId, ProcessState.IIK_MOUNT);
                } else {
                    textToSend = "наименование станции";
                    processStates.put(userId, ProcessState.DC_MOUNT);
                }
                editMessage(chatId, userId, "Введите " + textToSend + ": ");
            }
            default -> sendMessage(chatId, userId, "Неизвестное действие. Попробуйте еще раз.");
        }
    }

    private void concludeDeviceOperation(long userId, long chatId) {
        OtoType otoType = otoTypes.get(userId);
        Object typeIndicator = otoType != null ? otoType : processStates.get(userId);
        formingOtoLog(processInfo, typeIndicator);
        editTextAndButtons(actionConfirmation(null), confirmMenu, chatId, userId, 2);
    }

    private void clearData() {
        otoLog.clear();
        sequenceNumber = 0;
        processStates.clear();
        otoTypes.clear();
        processInfo = "";
        isPTO = false;
    }

    private void savePhoto(long userId, long chatId, PendingPhoto pending) {
        OtoType operationType = otoTypes.get(userId);
        String deviceNumber = pending.getDeviceNumber();
        // Получаем состояние загрузки фото (если нет, создаем новое)
        PhotoState photoState = photoStates.computeIfAbsent(userId, key -> new PhotoState(deviceNumber));
        if (isPTO || !PHOTO_SUBDIRS_NAME.containsKey(operationType)) { // Только фото ПТО
            handleUncontrolledPhoto(userId, chatId, pending);
            return;
        }
        handleChangingEquipmentPhoto(userId, chatId, pending, operationType, photoState);
    }

    /**
     * Обрабатывает фото, без дополнительных параметров сохранения
     */
    private void handleUncontrolledPhoto(long userId, long chatId, PendingPhoto pending) {
        doSave(userId, chatId, pending);
        pendingPhotos.remove(userId);
        editTextAndButtons("📸 Загрузите следующее фото или завершите загрузку.", CompleteButton, chatId, userId, 1);
    }

    /**
     * Обрабатывает фото, связанных с заменой оборудования
     */
    private void handleChangingEquipmentPhoto(long userId, long chatId, PendingPhoto pending, OtoType operationType, PhotoState photoState) {
        // Определяем, необходимость загрузки нового фото
        String photoPhase = photoState.getNextPhotoType(operationType);
        if (photoPhase == null) {
            editMessage(chatId, userId, "⚠ Ошибка: уже загружены все необходимые фото.");
            return;
        }
        // Сохранение фото
        doSave(userId, chatId, pending);
        photoState.markPhotoUploaded(photoPhase);

        addChangingInfo(pending);
        pendingPhotos.remove(userId);

        // Проверка необходимости продолжения загрузки фото
        if (photoState.isComplete(operationType)) {
            sendMessage(chatId, userId, "✅ Все фото загружены!");
            changeReasonInput(chatId, userId, operationType);

            photoStates.remove(userId);
        } else {
            // Рекомендации по загрузке фото
            sendNextPhotoInstruction(userId, chatId, photoState.getNextPhotoType(operationType));
            setProcessState(operationType, userId);
        }
    }

    private void changeReasonInput(long chatId, long userId, OtoType operationType) {
        editMessage(chatId, userId, "Введите причину замены: ");
        sequenceNumber = replacedEquipmentDatum.get(operationType).size();
        processStates.clear();
    }

    private void setProcessState(OtoType operationType, long userId) {
        switch (operationType) {
            case METER_CHANGE -> processStates.put(userId, ProcessState.WAITING_FOR_METER_PHOTO);
            case TT_CHANGE -> processStates.put(userId, ProcessState.WAITING_FOR_TT_PHOTO);
            default -> {
            }
        }
    }


    private void doSave(long userId, long chatId, PendingPhoto pending) {
//        OtoType operationType = otoTypes.get(userId);
        try {
            Path userDir = Paths.get(createSavingPath(pending, userId));

            Files.createDirectories(userDir);

            String newFileName = createNewFileName(pending, userId);
            Path destination = userDir.resolve(newFileName);

            // Сохранение
            Files.move(pending.getTempFilePath(), destination, StandardCopyOption.REPLACE_EXISTING);
            editMessage(chatId, userId, "Фото сохранено!\nФайл: " + newFileName);
        } catch (IOException e) {
            log.error("❌ Ошибка сохранения фото для userId {}: {}", userId, e.getMessage(), e);
            sendMessage(chatId, userId, "⚠ Ошибка при сохранении фото. Попробуйте снова.");
        }
    }

    private void sendNextPhotoInstruction(long userId, long chatId, String nextPhotoType) {
        if (nextPhotoType == null) return;

        String message = switch (nextPhotoType) {
            case "демонтирован" -> "📸 Пожалуйста, загрузите фото **ДЕМОНТИРОВАННОГО** прибора и введите показания.";
            case "установлен" -> "📸 Пожалуйста, загрузите фото **УСТАНОВЛЕННОГО** прибора и введите "
                    + (processStates.get(userId).equals(ProcessState.IIK_WORKS) ? "показания" : "его номер");
            case "ф.A" -> "📸 Прикрепите фото **ТТ фазы A** и введите его номер:";
            case "ф.B" -> "📸 Прикрепите фото **ТТ фазы B** и введите его номер:";
            case "ф.C" -> "📸 Прикрепите фото **ТТ фазы C** и введите его номер:";
            default -> null;
        };

        if (message != null) {
            sendMessage(chatId, userId, message);
        }
    }

    private String createSavingPath(PendingPhoto pending, long userId) {

        OtoType operationType = otoTypes.get(userId);
        String baseDir = PHOTO_PATH + File.separator;
        String path = savingPaths.getOrDefault(pending.getDeviceNumber(), "unknown");

        if (operationType != null) {
            if (PHOTO_SUBDIRS_NAME.containsKey(operationType)) {
                baseDir += PHOTO_SUBDIRS_NAME.get(operationType) + File.separator;
            }
        } else if (!processStates.get(userId).equals(ProcessState.WAITING_FOR_DC_PHOTO))
            path = path.substring(0, path.lastIndexOf("\\"));

        if (photoStates.get(userId).getUploadedPhotos().isEmpty()) chgePath = path;
        else if (photoStates.get(userId).getUploadedPhotos().size() < 2) path = chgePath;
        return baseDir + path;
    }

    private String createNewFileName(PendingPhoto pending, Long userId) {
        OtoType operationType = otoTypes.get(userId);
        String photoSuffix = "";
        String additionalInfo = pending.getAdditionalInfo() != null ? "_" + pending.getAdditionalInfo() : "";
        if (operationType != null) {
            PhotoState photoState = photoStates.get(userId);
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

    private void registerUser(Update update) {
        User newUser = userService.createUser(update);
        userService.registerUser(newUser);
//        if (sequenceNumber < registrationMenu.size()) {
//            sendMessage(chatId, registrationMenu.get(sequenceNumber));
//            sequenceNumber++;
//        }
    }

    private void sendMessage(long chatId, long userId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message, chatId);
    }

    private void editMessage(long chatId, long userId, String newTextToReplace) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(sendMessagesIds.get(userId));
        editMessage.setText(newTextToReplace);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message, long userId) {
        try {
            Message sentMessage = execute(message); // Отправляем сообщение в Telegram
            sendMessagesIds.put(userId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    public void sendTextMessage(String text, Map<String, String> buttons, Long chatId, long userId, int columns) {
        try {
            SendMessage message = createMessage(text, buttons, chatId, columns);
            var task = sendApiMethodAsync(message);
            Message sentMessage = task.get();
            this.sentMessages.add(sentMessage);
            sendMessagesIds.put(userId, sentMessage.getMessageId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void editTextAndButtons(String text, Map<String, String> buttons, Long chatId, Long userId, int columns) {
        Integer messageId = sendMessagesIds.get(userId);
        if (messageId == null) {
            log.warn("Нет messageId для userId {}", userId);
            return;
        }

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        editMessage.setParseMode("markdown");

        // Создаём временный объект для формирования разметки
        SendMessage temp = new SendMessage();
        attachButtons(temp, buttons, columns);
        editMessage.setReplyMarkup((InlineKeyboardMarkup) temp.getReplyMarkup());

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }


    public SendMessage createMessage(String text, Map<String, String> buttons, Long userId, int columns) {
        SendMessage message = createMessage(text, userId);
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

    public Long getCurrentuserId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }

        return null;
    }

    private void sheetsFilling(long userId) {
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
            boolean dataContainsNotNot5 = logData.contains("НОТ") || logData.contains("НОТ5");

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
                    if (addedRows == otoLog.size()) isLogFilled = true;
                }
                if (dataContainsNot123) {
                    excelFileService.clearCellData(getIndexesOfCleaningCells(not123ColumnsToClear, meterSheet), otoRow);
                    String notType = taskOrder.substring(taskOrder.indexOf("(") + 1, taskOrder.indexOf("(") + 1 + 4);
                    otoRow.getCell(excelFileService.findColumnIndex(meterSheet, "Текущее состояние")).setCellValue(notType);
                }
                if (dataContainsNotNot5) {
                    String notType = taskOrder.substring(taskOrder.indexOf("НОТ"), taskOrder.indexOf("НОТ") + 3);
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
                String mountingMeterNumber = dataParts[2];
                Meter m = meterService.getMeterByNumber(deviceNumber);
                MeteringPoint mp = meteringPointService.getIikByMeterId(m.getId());
                MeteringPoint nmp = new MeteringPoint();
                nmp.setMeter(meterService.getMeterByNumber(mountingMeterNumber));
                meteringPointService.update(nmp, mp.getId());
                Object mountingDeviceNumber = parseMeterNumber(mountingMeterNumber);
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
            case "iikMount" -> {
                MeteringPoint nmp = new MeteringPoint();
                String stationName = otoRow.getCell(6).getStringCellValue();
                String substationName = otoRow.getCell(7).getStringCellValue();
                String mountingMeterNumber = dataParts[3];
                String meterType = dataParts[4].toUpperCase();
                String meteringPointName = dataParts[5];
                String meteringPointAddress = dataParts[6];
                String meterPlacement = dataParts[7];
                String mountOrg = dataParts[9];
                LocalDate meteringPointMountDate = LocalDate.parse(dataParts[10], FileManagement.DD_MM_YYYY);
                Substation s = substationService.findByName(substationName, stationName).orElseGet(null);
                if (s == null) {
                    s = ptoService.createSubstationIfNotExists(otoRow);
                }
                nmp.setId(meteringPointService.getNextId());
                nmp.setInstallationDate(meteringPointMountDate);
                nmp.setSubstation(s);
                nmp.setName(meteringPointName);
                nmp.setMeteringPointAddress(meteringPointAddress);
                nmp.setMeterPlacement(meterPlacement);
                nmp.setMountOrganization(mountOrg);
                Meter newMeteringPointMeter = Optional.ofNullable(
                                meterService.getMeterByNumber(mountingMeterNumber)
                        )
                        .orElseGet(() -> {
                            Meter created = ptoService.createMeter(mountingMeterNumber, meterType, deviceNumber);
                            return meterService.create(created);
                        });

                if (!isMeterInstalled(mountingMeterNumber)) {
                    nmp.setMeter(newMeteringPointMeter);
                } else {
                    log.warn("Данный прибор учета уже установлен на другой точке учёта");
                }

                meteringPointService.create(nmp);

                Object mountingDeviceNumber = parseMeterNumber(dataParts[3]); //Номер счетчика
                if (mountingDeviceNumber instanceof Long) {
                    otoRow.getCell(13).setCellValue((Long) mountingDeviceNumber);
                } else {
                    otoRow.getCell(13).setCellValue((String) mountingDeviceNumber);
                }
                otoRow.getCell(9).setCellValue(meteringPointName); //Наименование точки учёта
                otoRow.getCell(10).setCellValue(meterPlacement); // Место установки счетчика (Размещение счетчика)
                otoRow.getCell(11).setCellValue(meteringPointAddress); // Адрес установки
                otoRow.getCell(12).setCellValue(meterType); // Марка счётчика
                Object mountDeviceNumber = parseMeterNumber(mountingMeterNumber);
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
        newLogRow.createCell(19);
//        newLogRow.getCell(19).setCellValue("");
        newLogRow.getCell(20).setCellValue("Исполнитель"); //TODO: взять исполнителя из БД по userId
        newLogRow.getCell(21).setCellValue(taskOrder);
        otoRow.getCell(orderColumnNumber).setCellValue(taskOrder);
        return taskOrder;

//      newLogRow.createCell(22).setCellValue("Выполнено");   //TODO: добавить после реализации внесения корректировок в Горизонт либо БД
    }

    private boolean isMeterInstalled(String meterNum) {
        return ptoService.entityCache
                .get(PtoService.EntityType.METERING_POINT)
                .values()
                .stream()
                .map(o -> (MeteringPoint) o)
                .map(MeteringPoint::getMeter)
                .filter(Objects::nonNull)
                .anyMatch(m -> meterNum.equals(m.getMeterNumber()));
    }

    private static List<String> getStrings(String data) {
        return fillingData.get(data);
    }

    private String actionConfirmation(Long userId) {
        StringBuilder resultStr = new StringBuilder("Выполнены следующие действия:\n");
        int lineCounter = 0;

        for (Map.Entry<String, String> entry : otoLog.entrySet()) {
            String key = entry.getKey();
            String[] str = entry.getValue().split("_");
            str[4] = str[4].toUpperCase();
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
                case "iikMount" -> resultStr.append(String.format(
                        "\nНаименование ТУ: %s, \nПрибор учёта: %s №: %s. \nСтанция: %s, \nТП/КТП: %s, \nАдрес: %s, \nДата монтажа: %s.",
                        str[5], str[4], str[3], str[1], str[2], str[6], str[10]));
                default -> {
                    String device = ProcessState.IIK_WORKS.equals(processStates.get(userId)) ? " ПУ" : " Концентратор";
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
                case IIK_MOUNT -> "iikMount";
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

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
