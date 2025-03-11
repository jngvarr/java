package jngvarr.ru.pto_ackye_rzhd.telegram;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import jngvarr.ru.pto_ackye_rzhd.config.BotConfig;
import jngvarr.ru.pto_ackye_rzhd.services.UserServiceImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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

    public enum UserState {
        WAITING_FOR_COUNTER_PHOTO,
        WAITING_FOR_DC_PHOTO,
        WAITING_FOR_CORRECT_BARCODE,
        MANUAL_INSERT,
        WAITING_FOR_METER_READING,
        NONE
    }

    public enum equipmentType {
        COUNTER, DC, TT
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
    private Map<Long, OtoIIK> otoIIKTypes = new HashMap<>();
    private Map<Long, equipmentType> workEquipment = new HashMap<>();
    private Map<String, String> otoIIKLog = new HashMap<>();
    private List<String> deviceInfo = new ArrayList<>();

    // Карта для хранения информации о фото, ожидающих подтверждения
    private Map<Long, PendingPhoto> pendingPhotos = new HashMap<>();

    public enum OtoIIK {
        WK_DROP,
        METER_CHANGE,
        SET_NOT,
        SUPPLY_RESTORING,
        TT_CHANGE
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

    public TBot(BotConfig config, UserServiceImpl service) {
        super(config.getBotToken());
        this.config = config;
        this.service = service;

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
        if (OtoIIK.METER_CHANGE.equals(otoIIKTypes.get(chatId)) && photoCounter >= 2) {

            return;
        }

        if (OtoIIK.METER_CHANGE.equals(otoIIKTypes.get(chatId))) {
            photoCounter++;
        }


        // Проверяем, есть ли подпись к фото
        String manualReading = update.getMessage().getCaption();

        // Если фото не запрашивалось
        if (!userStates.containsKey(chatId)) {
            sendMessage(chatId, "Фото не запрашивалось. Если хотите начать, нажмите /start", null);
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
                sendMessage(chatId, "Не удалось обработать изображение.", null);
                return;
            }
            // Files.deleteIfExists(tempFilePath);
            // 4. Декодируем штрихкод
            String barcodeText = decodeBarcode(bufferedImage);
            if (barcodeText == null) {
                barcodeText = decodeBarcode(resizeImage(bufferedImage, bufferedImage.getWidth() * 2, bufferedImage.getHeight() * 2));
            }
            if (barcodeText == null) {
                barcodeText = decodeBarcode(convertToGrayscale(bufferedImage));
            }

            // 5. Определяем тип фото (счётчик или концентратор)
            String type = (currentState == UserState.WAITING_FOR_COUNTER_PHOTO || workEquipment.get(chatId) == equipmentType.COUNTER) ? "counter" : "concentrator";

            // 6. Создаём объект для хранения фото
            PendingPhoto pendingPhoto = new PendingPhoto(type, tempFilePath, barcodeText);
            pendingPhotos.put(chatId, pendingPhoto);
            if (manualReading != null) pendingPhoto.setMeterReading(manualReading.trim());

            // 7. Если штрихкод найден и есть показания – сразу сохраняем
            if (barcodeText != null && pendingPhoto.getMeterReading() != null) {
                savePhoto(chatId, pendingPhoto);
            } else if (barcodeText == null) {
                sendMessage(chatId, "Штрихкод не найден. Введите номер ПУ вручную:", null);
                userStates.put(chatId, UserState.MANUAL_INSERT);
            } else {
                sendMessage(chatId, "Введите показания счётчика:", null);
                userStates.put(chatId, UserState.WAITING_FOR_METER_READING);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки фото: " + e.getMessage());
            sendMessage(chatId, "Произошла ошибка при обработке фото.", null);
        }
    }


    private void handleTextMessage(Update update) {
        String msgText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if (userStates.get(chatId) == UserState.MANUAL_INSERT) {
            String deviceNumber = update.getMessage().getText().trim();
            PendingPhoto pending = pendingPhotos.get(chatId);
            if (pending != null) {
                pending.setScannedBarcode(deviceNumber);
                savePhoto(chatId, pending);
            } else {
                sendMessage(chatId, "Ошибка: нет ожидающих фото для привязки данных.", null);
            }
            return;
        }

        if (userStates.get(chatId) == UserState.WAITING_FOR_METER_READING) {
            String deviceIndication = update.getMessage().getText().trim();

            PendingPhoto pending = pendingPhotos.get(chatId);
            if (pending != null) {
                pending.setMeterReading(deviceIndication);
                savePhoto(chatId, pending);
            } else {
                sendMessage(chatId, "Ошибка: нет ожидающих фото для привязки показаний.", null);
            }
            return;
        }
        OtoIIK otoIIKType = otoIIKTypes.get(chatId);
        Map<OtoIIK, String> otoIIKStringMap = Map.of(
                OtoIIK.WK_DROP, "WK_",
                OtoIIK.SET_NOT, "NOT_",
                OtoIIK.SUPPLY_RESTORING, "SUPPLY_");

        if (otoIIKType == OtoIIK.WK_DROP || otoIIKType == OtoIIK.SET_NOT || otoIIKType == OtoIIK.SUPPLY_RESTORING) {
            String deviceNumber = update.getMessage().getText().trim();
            if (deviceNumber.contains("_")) {
                String[] deviceNumberData = deviceNumber.split("_");
                deviceNumber = deviceNumberData[0];
                otoIIKLog.put(deviceNumber, otoIIKStringMap.get(otoIIKType) + deviceNumberData[1]);
            } else otoIIKLog.put(deviceNumber, otoIIKStringMap.get(otoIIKType));
            log.info("{}. ПУ №: {}", ++sequenceNumber, deviceNumber);
            sendTextMessage("Введите номер следующего ПУ или закончите ввод.", CompleteButton, chatId, 1);
            return;
        }

        if (otoIIKType == OtoIIK.METER_CHANGE) {
            deviceInfo.add(update.getMessage().getText());
            ++sequenceNumber;
            if (sequenceNumber < 2) {
                sendMessage(chatId, "Введите номер и показания устанавливаемого ПУ.");
            } else {
                formingOtoIikLogWithMeterChange(deviceInfo);
                sendTextMessage(meterChangeConfirmation(otoIIKLog), confirmMenu, chatId, 2);
            }
            return;
        }

        // Обработка остальных текстовых сообщений
        switch (msgText) {
            case "/start" -> handleStartCommand(chatId, update.getMessage().getChat().getFirstName());
            case "/help" -> sendMessage(chatId, HELP, null);
            case "/register" -> registerUser(chatId);
            default -> sendMessage(chatId, "Команда не распознана. Попробуйте еще раз.", null);
        }
    }

    private void formingOtoIikLogWithMeterChange(List<String> deviceInfo) {
        String[] deviceNumber = deviceInfo.get(0).split("_");
        otoIIKLog.put(deviceNumber[0], "meterChange_" + deviceNumber[1] + "_" + deviceInfo.get(1));
    }

    private String meterChangeConfirmation(Map<String, String> otoIIKLog) {
        StringBuilder resultStr = new StringBuilder("Выполняемое действие:\n");
        for (Map.Entry<String, String> entry : otoIIKLog.entrySet()) {
            String[] str = entry.getValue().split("_");
            resultStr
                    .append("Замена прибора учета № ")
                    .append(entry.getKey())
                    .append(" с показаниями: ")
                    .append(str[1])
                    .append("\n на  прибор учета № ")
                    .append(str[2])
                    .append(" с показаниями: ")
                    .append(str[3]) //TODO объединить
                    .append("\n");
        }
        return resultStr.toString();
    }

    private String actionConfirmation(Map<String, String> otoIIKLog) {
        int lineCounter = 0;
        StringBuilder resultStr = new StringBuilder("Выполнены следующие действия: ");
        for (Map.Entry<String, String> entry : otoIIKLog.entrySet()) {
            String[] str = entry.getValue().split("_");
            String[] key = entry.getKey().split("_");
            List<String> strings = getStrings(str[0]);
            resultStr
                    .append("\n")
                    .append(++lineCounter)
                    .append(". ПУ № ")
                    .append(key[0])
                    .append(strings.get(2));
            if (str.length > 1) resultStr.append(" ").append(str[str.length - 1]).append(".");
        }
        return resultStr.toString();
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
                sendMessage(chatId, "Пожалуйста, загрузите фото счетчика.", null);
                userStates.put(chatId, UserState.WAITING_FOR_COUNTER_PHOTO);
                workEquipment.put(chatId, equipmentType.COUNTER);
            }
            case "ptoIVKE" -> {
                sendMessage(chatId, "Пожалуйста, загрузите фото концентратора.", null);
                userStates.put(chatId, UserState.WAITING_FOR_DC_PHOTO);
            }
            case "otoIIK" -> {
                sendTextMessage("Выберите вид ОТО ИИК: ", otoIIKButtons, chatId, 2);
                workEquipment.put(chatId, equipmentType.COUNTER);
            }
            case "otoIVKE" -> {
                sendTextMessage("Выберите вид ОТО ИВКЭ: ", otoIVKEButtons, chatId, 2);
            }

            case "wkDrop" -> {
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoIIKTypes.put(chatId, OtoIIK.WK_DROP);
            }

            case "setNot" -> {
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoIIKTypes.put(chatId, OtoIIK.SET_NOT);
            }

            case "ttChange" -> {
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoIIKTypes.put(chatId, OtoIIK.TT_CHANGE);
            }

            case "powerSupplyRestoring" -> {
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoIIKTypes.put(chatId, OtoIIK.SUPPLY_RESTORING);
            }

            case "meterChange" -> {
                sendTextMessage("Вид передачи данных: ",
                        Map.of("С приложением фото.", "meterChangeWithPhoto",
                                "Без приложения фото.", "meterChangeWithoutPhoto"), chatId, 2);
            }

            case "meterChangeWithPhoto" -> {
                sendMessage(chatId, "Прикрепите фото демонтируемого прибора учета: ");
                otoIIKTypes.put(chatId, OtoIIK.METER_CHANGE);
                userStates.put(chatId, UserState.WAITING_FOR_COUNTER_PHOTO);
            }

            case "meterChangeWithoutPhoto" -> {
                sendMessage(chatId, "Введите номер и показания демонтируемого прибора учета: \n" +
                        "например: 7200123456_7890");
                otoIIKTypes.put(chatId, OtoIIK.METER_CHANGE);
            }

            case "LOADING_COMPLETE" -> {
                sendTextMessage(actionConfirmation(otoIIKLog), confirmMenu, chatId, 2);
            }

            case "confirm", "cancel" -> {
                String textToSend;
                if ("confirm".equals(callbackData)) {
                    textToSend = "Информация сохранена.";
                    sendMessage(chatId, "Подождите, идёт загрузка данных...", null);
                    operationLogFilling(otoIIKLog, true);
                } else {
                    textToSend = "Информация не сохранена.";
                }
                sendMessage(chatId, textToSend, null);
                clearData();
                sendMessage(chatId, "Для продолжения снова нажмите /start", null);
            }

            default -> sendMessage(chatId, "Неизвестное действие. Попробуйте еще раз.", null);
        }
    }

    private void clearData() {
        otoIIKLog.clear();
        sequenceNumber = 0;
        userStates.clear();
        otoIIKTypes.clear();
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

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }

    private void savePhoto(long chatId, PendingPhoto pending) {
        try {
            String baseDir = PHOTO_PATH + File.separator;
            if (otoIIKTypes.get(chatId).equals(OtoIIK.METER_CHANGE)) baseDir += "Замена ПУ" + File.separator;
            String path = savingPaths.get(pending.getScannedBarcode());
            String resultPath = !otoIIKTypes.get(chatId).equals(OtoIIK.METER_CHANGE) ? path.substring(0, path.lastIndexOf("\\")) : path;
            if (photoCounter == 1) chgePath = resultPath;
            if (photoCounter == 2) resultPath = chgePath;
            Path userDir = Paths.get(baseDir + resultPath);
            Files.createDirectories(userDir);

            String pendingPhotoType = pending.getType();

            String prefix = switch (pendingPhotoType) {
                case "counter" -> "ИИК_";
                case "concentrator" -> "ИВКЭ_";
                default -> "unknown_";
            };


            String barcode = (pending.getScannedBarcode() != null) ? pending.getScannedBarcode() : "unknown";
            String indication = (pending.getMeterReading() != null) ? "_" + pending.getMeterReading() : "";
            String newFileName;
            deviceInfo.add(barcode + indication);
            if (photoCounter == 1) {
                newFileName = formattedCurrentDate + "_" + prefix + barcode + indication + "_демонтирован.jpg";
            } else if (photoCounter == 2) {
                newFileName = formattedCurrentDate + "_" + prefix + barcode + indication + "_установлен.jpg";
                formingOtoIikLogWithMeterChange(deviceInfo);
            } else newFileName = formattedCurrentDate + "_" + prefix + barcode + indication + ".jpg";

            Path destination = userDir.resolve(newFileName);

            Files.move(pending.getTempFilePath(), destination, StandardCopyOption.REPLACE_EXISTING);
            sendMessage(chatId, "Фото сохранено!\nФайл: " + newFileName, null);

            pendingPhotos.remove(chatId);

            if (photoCounter >= 2) {
                otoIIKTypes.clear();
                userStates.clear();
                sendTextMessage("Фото сохранены, пожалуйста завершите загрузку.", CompleteButton, chatId, 1);
                photoCounter = 0;
                return;
            }

            if (otoIIKTypes.containsKey(chatId)) {
                sendMessage(chatId, "Прикрепите фото устанавливаемого прибора учета:\n", null);
            } else
                sendTextMessage("Заргрузите следующее фото или закончите загрузку.", CompleteButton, chatId, 1);

        } catch (
                Exception e) {
            log.error("Ошибка сохранения фото: " + e.getMessage());
            sendMessage(chatId, "Произошла ошибка при сохранении фото.", null);
        }
    }

    /**
     * Метод декодирования штрихкода с изображения с помощью ZXing.
     *
     * @param image BufferedImage, считанное из файла.
     * @return текст штрихкода или null, если декодирование не удалось.
     */
    private String decodeBarcode(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            log.warn("Штрихкод не найден при первой попытке: {}", e.getMessage());
            // Попытка декодирования при повороте изображения
            for (int angle = 90; angle < 360; angle += 30) {
                BufferedImage rotated = rotateImage(image, angle);
                try {
                    LuminanceSource rotatedSource = new BufferedImageLuminanceSource(rotated);
                    BinaryBitmap rotatedBitmap = new BinaryBitmap(new HybridBinarizer(rotatedSource));
                    Result rotatedResult = new MultiFormatReader().decode(rotatedBitmap);
                    return rotatedResult.getText();
                } catch (NotFoundException ignored) {
                    // Продолжаем, если штрихкод не найден
                }
            }
            return null;
        }
    }

    /**
     * Метод разворота изображения с помощью.
     *
     * @param src   BufferedImage, считанное из файла,
     * @param angle int, угол поворота изображения
     *              * @return BufferedImage повернутое на заданный угол исходное изображение
     */
    private BufferedImage rotateImage(BufferedImage src, int angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads));
        double cos = Math.abs(Math.cos(rads));
        int w = src.getWidth();
        int h = src.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, src.getType());
        Graphics2D g2d = rotated.createGraphics();
        g2d.translate((newWidth - w) / 2, (newHeight - h) / 2);
        g2d.rotate(rads, w / 2, h / 2);
        g2d.drawRenderedImage(src, null);
        g2d.dispose();
        return rotated;
    }

    // Преобразование цветного изображение в оттенки серого
    private BufferedImage convertToGrayscale(BufferedImage src) {
        BufferedImage gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = gray.getGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return gray;
    }


    private void registerUser(long chatId) {
        InlineKeyboardMarkup keyboardMarkup = createInlineKeyboardMarkup(Map.of(
                "Yes", YES_BUTTON,
                "No", NO_BUTTON
        ));

        sendMessage(chatId, "Do you really want to register?", keyboardMarkup);
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

    private void sendMessage(long chatId, String textToSend, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        if (replyKeyboard instanceof ReplyKeyboardMarkup) {
            message.setReplyMarkup(replyKeyboard);
        } else if (replyKeyboard instanceof InlineKeyboardMarkup) {
            message.setReplyMarkup(replyKeyboard);
        }
        executeMessage(message);
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
            int meterNumberColumnIndex = findColumnIndex(workSheet, "Номер счетчика");
            int orderColumnNumber = findColumnIndex(workSheet, "Отчет бригады о выполнении ОТО");

            CellStyle commonCellStyle = createCommonCellStyle(operationLog);
            CellStyle dateCellStyle = createDateCellStyle(operationLog, "dd.MM.YYYY", "Calibri");

            int addRow = 0;
            for (Row row : workSheet) {
                String meterNumber = getCellStringValue(row.getCell(meterNumberColumnIndex));
                String logData = opLog.getOrDefault(meterNumber, "");
                if (!logData.isEmpty()) {
                    Row newRow = operationLogSheet.createRow(operationLogLastRowNumber + ++addRow);
                    copyRow(row, newRow, orderColumnNumber, commonCellStyle, dateCellStyle);
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
        switch (data) {
            case "WK", "NOT", "SUPPLY" -> {
                String[] additionalData = logData.split("_");
                List<String> columns = getStrings(data);
                Cell date = newRow.getCell(16);
                setDateCellStyle(date);
                newRow.getCell(17).setCellValue(columns.get(0));
                newRow.getCell(18).setCellValue(columns.get(1));
                newRow.getCell(20).setCellValue("Исполнитель"); //TODO: взять исполнителя из бд по chatId
                String taskOrder = straightFormattedCurrentDate + columns.get(2) + (additionalData.length > 1 ? " " + additionalData[1] : "");
                newRow.createCell(21).setCellValue(taskOrder);
//                newRow.createCell(22).setCellValue("Выполнено");//TODO: добавить после реализации внесения корректировок в Горизонт либо БД

                // Внесение данных в журнал "Контроль ПУ РРЭ"
                otoRow.getCell(orderColIndex).setCellValue(taskOrder);
            }
            case "meterChange" -> {
                String[] replacingData = logData.split("_");

                Cell date = newRow.getCell(16);
                setDateCellStyle(date);

                newRow.getCell(17).setCellValue("Нет связи со счетчиком");
                newRow.getCell(18).setCellValue("Неисправность счетчика (счетчик заменен)");
                newRow.getCell(20).setCellValue("Исполнитель"); //TODO: взять исполнителя из бд по chatId
                String meterNumberStr = replacingData[2];

                Object mountingMeterNumber = parseMeterNumber(meterNumberStr);

                String taskOrder = straightFormattedCurrentDate + " - замена счетчика "
                        + getCellStringValue(newRow.getCell(13)) + " (" + replacingData[1] + " кВт) на "
                        + replacingData[2] + " (" + replacingData[3] + " кВт).";
                newRow.createCell(21).setCellValue(taskOrder);
//                newRow.createCell(22).setCellValue("Выполнено");//TODO: добавить после реализации внесения корректировок в Горизонт либо БД

                // Внесение данных в журнал "Контроль ПУ РРЭ"
                if (mountingMeterNumber instanceof Long) {
                    otoRow.getCell(meterNumColIndex).setCellValue((Long) mountingMeterNumber);
                } else {
                    otoRow.getCell(meterNumColIndex).setCellValue((String) mountingMeterNumber);
                }
                otoRow.getCell(orderColIndex).setCellValue(taskOrder);
            }
        }
    }

    private static List<String> getStrings(String data) {
        Map<String, List<String>> fillingData = Map.of(
                "WK", List.of("Нет связи со счетчиком",
                        "Ошибка ключа - Вронгкей (сделана прошивка счетчика)",
                        " - Сброшена ошибка ключа Вронгкей (счетчик не на связи)."),
                "NOT", List.of("Нет связи со счетчиком",
                        "Уточнение реквизитов ТУ (подана заявка на корректировку НСИ)", " - НОТ."),
                "SUPPLY", List.of("Нет связи со счетчиком", "Восстановление схемы.", " - Восстановление схемы подключения."));

        return fillingData.get(data);
    }

    /**
     * Метод преобразования значения ячейки в дату, так чтоб Excel понимал его именно как дату.
     *
     * @param date Cell, ячейка содержащая значеие даты.
     */
    private void setDateCellStyle(Cell date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        CellStyle dateStyle = createDateCellStyle(date.getRow().getSheet().getWorkbook(), "dd.MM.yy", "Arial");
        try {
            date.setCellValue(sdf.parse(straightFormattedCurrentDate));
        } catch (ParseException e) {
            date.setCellStyle(dateStyle);
        }
        date.setCellStyle(dateStyle);
    }

    private static void copyRow(Row sourceRow, Row targetRow, int columnCount, CellStyle defaultCellStyle, CellStyle dateCellStyle) {
        for (int i = 0; i <= columnCount; i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);

            if (sourceCell != null) {
                switch (sourceCell.getCellType()) {
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(sourceCell)) {
                            targetCell.setCellValue(sourceCell.getDateCellValue()); // Копируем дату
                            targetCell.setCellStyle(dateCellStyle); // Применяем стиль для даты
                        } else {
                            targetCell.setCellValue(sourceCell.getNumericCellValue()); // Копируем число
                            targetCell.setCellStyle(defaultCellStyle);
                        }
                        break;
                    case BOOLEAN:
                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                    case FORMULA:
                        targetCell.setCellFormula(sourceCell.getCellFormula());
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                    default:
                        targetCell.setCellStyle(defaultCellStyle);
                        break;
                }
            }
        }
    }

    private Map<String, String> getPhotoSavingPathFromExcel() {

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
            int meterNumberColumnIndex = findColumnIndex(iikSheet, "Номер счетчика");
            int eelColumnIndex = findColumnIndex(iikSheet, "ЭЭЛ");
            int stationColumnIndex = findColumnIndex(iikSheet, "Железнодорожная станция");
            int substationColumnIndex = findColumnIndex(iikSheet, "ТП/КТП");
            int meterPointIndex = findColumnIndex(iikSheet, "Точка Учета");
            for (Row row : iikSheet) {
                String meterNum = getCellStringValue(row.getCell(meterNumberColumnIndex));
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

    private static String getCellStringValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return new SimpleDateFormat("dd.MM.yyyy").format(cell.getDateCellValue());
                    } else {
                        return new DecimalFormat("0").format(cell.getNumericCellValue());
                    }
                case FORMULA:
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    return evaluator.evaluate(cell).getStringValue();
                default:
                    return null;
            }
        }
        return null;
    }

    private static int findColumnIndex(Sheet sheet, String columnName) {
        Row headerRow = sheet.getRow(0); // Заголовок на первой строке
        if (headerRow == null) return -1;

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && columnName.equalsIgnoreCase(cell.getStringCellValue())) {
                return i;
            }
        }
        return -1;
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

    private static CellStyle createDateCellStyle(Workbook resultWorkbook, String format, String font) {
        CellStyle dateCellStyle = resultWorkbook.createCellStyle();
        DataFormat dateFormat = resultWorkbook.createDataFormat(); // Формат даты
        dateCellStyle.setDataFormat(dateFormat.getFormat(format));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setFont(createCellFontStyle(resultWorkbook, font, (short) 10, false));
        return dateCellStyle;
    }


    static CellStyle createCommonCellStyle(Workbook resultWorkbook) {
        CellStyle simpleCellStyle = resultWorkbook.createCellStyle();
        Font font = createCellFontStyle(resultWorkbook, "Arial", (short) 10, false);

        simpleCellStyle.setBorderBottom(BorderStyle.THIN);
        simpleCellStyle.setBorderLeft(BorderStyle.THIN);
        simpleCellStyle.setBorderRight(BorderStyle.THIN);
        simpleCellStyle.setBorderTop(BorderStyle.THIN);
        simpleCellStyle.setFont(font);
        return simpleCellStyle;
    }

    private static Font createCellFontStyle(Workbook workbook, String fontName, short fontSize, boolean isBold) {
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        font.setBold(isBold);
        return font;
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
