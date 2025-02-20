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
import org.springframework.format.annotation.DateTimeFormat;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static jngvarr.ru.pto_ackye_rzhd.telegram.FileManagement.*;
import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.*;
import static org.openxmlformats.schemas.spreadsheetml.x2006.main.STTimePeriod.TODAY;


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
    private Map<String, String> otoIIKLog = new HashMap<>();

    // Карта для хранения информации о фото, ожидающих подтверждения
    private Map<Long, PendingPhoto> pendingPhotos = new HashMap<>();

    public enum OtoIIK {
        WK_DROP
    }

    private int attempt = 0;
    private Map<String, String> otoIIKButtons = Map.of(
            "Сброшена ошибка ключа (WK)", "WKDrop",
            "Замена счетчика", "meterChange",
            "Замена трансформаторов тока", "ttChange",
            "Восстановление питания ТУ", "powerSupplyRestoring",
            "Присвоение статуса НОТ", "NOT",
            "Другие работы", "otherIIK");
    private Map<String, String> otoIVKEButtons = Map.of(
            "Замена концентратора", "dcChange",
            "Перезагрузка концентратора", "dcRestart",
            "Восстановление питания", "powerSupplyRestoring",
            "Другие работы", "otherDC");

    private Map<String, String> savingPaths = getPhotoSavingPathFromExcel();

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
                Files.copy(in, tempFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
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
            String type = (currentState == UserState.WAITING_FOR_COUNTER_PHOTO) ? "counter" : "concentrator";

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

        if (otoIIKTypes.get(chatId) == OtoIIK.WK_DROP) {
            String deviceNumber = update.getMessage().getText().trim();
            otoIIKLog.put(deviceNumber, "WK");
            log.info("ПУ № {}, попытка №: {}", deviceNumber, ++attempt);
            sendTextMessage("Введите номер следующего ПУ или закончите ввод.", Map.of("Закончить ввод", "LOADING_COMPLETE"), chatId );
            return;
        }

        // Обработка остальных текстовых сообщений
        switch (msgText) {
            case "/start" -> handleStartCommand(chatId, update.getMessage().getChat().getFirstName());
            case "/help" -> sendMessage(chatId, PtoTelegramBotContent.HELP, null);
            case "/register" -> registerUser(chatId);
            default -> sendMessage(chatId, "Команда не распознана. Попробуйте еще раз.", null);
        }
    }


    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (callbackData) {
            case "newTU" -> {
                sendMessage(chatId, PtoTelegramBotContent.NEW_TU, null);
                sendTextMessage("Выберити выд работ: ", modes.get("newTU"), chatId);
            }
            case "pto" -> {
                sendMessage(chatId, PtoTelegramBotContent.PTO, null);
                sendTextMessage("Выберити выд работ: ", modes.get("pto"), chatId);
            }
            case "oto" -> {
                sendMessage(chatId, PtoTelegramBotContent.OTO, null);
                sendTextMessage("Выберити выд работ: ", modes.get("oto"), chatId);
            }
            // Обработка выбора для счетчика и концентратора
            case "ptoIIK" -> {
                sendMessage(chatId, "Пожалуйста, загрузите фото счетчика.", null);
                userStates.put(chatId, UserState.WAITING_FOR_COUNTER_PHOTO);
            }
            case "ptoIVKE" -> {
                sendMessage(chatId, "Пожалуйста, загрузите фото концентратора.", null);
                userStates.put(chatId, UserState.WAITING_FOR_DC_PHOTO);
            }
            case "otoIIK" -> {
                sendTextMessage("Выбирете вид ОТО ИИК: ", otoIIKButtons, chatId);
            }
            case "otoIVKE" -> {
                sendTextMessage("Выбирете вид ОТО ИВКЭ: ", otoIVKEButtons, chatId);
            }

            case "LOADING_COMPLETE" -> {
                userStates.clear();
                sendMessage(chatId, "Загрузка окончена. Для продолжения снова нажмите /start", null);
                operationLogFilling(otoIIKLog, true);
            }

            case "WKDrop" -> {
                sendMessage(chatId, "Введите номер прибора учета: ");
                otoIIKTypes.put(chatId, OtoIIK.WK_DROP);
            }

            default -> sendMessage(chatId, "Неизвестное действие. Попробуйте еще раз.", null);
        }
    }


    private void handleStartCommand(long chatId, String firstName) {
//        String welcomeMessage = String.format("Приветствую тебя, пользователь %s, Что будем делать?", firstName);
        log.info("Replied to user: {}", firstName);
//        sendMessage(chatId, welcomeMessage, null);

        sendTextMessage(MAIN_MENU, Map.of(
                "ПТО", "pto",
                "ОТО", "oto",
                "Монтаж / демонтаж ТУ", "newTU"
        ), chatId);
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
            String baseDir = PHOTO_PATH + java.io.File.separator;
            Path userDir = Paths.get(baseDir + savingPaths.get(pending.getScannedBarcode()));
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }
            String pendingPhotoType = pending.getType();

            String prefix = switch (pendingPhotoType) {
                case "counter" -> "ИИК_";
                case "concentrator" -> "ИВКЭ_";
                default -> "unknown_";
            };


            Map<String, String> saveButtons = Map.of("Завершить загрузку фото", "LOADING_COMPLETE",
                    "Загрузить следующую фотографию", "counter".equals(pendingPhotoType) ? "ptoIIK" : "ptoIVKE");

            String barcode = (pending.getScannedBarcode() != null) ? pending.getScannedBarcode() : "unknown";
            String reading = (pending.getMeterReading() != null) ? "_" + pending.getMeterReading() : "";
            String newFileName = formattedCurrentDate + "_" + prefix + barcode + reading + ".jpg";
            Path destination = userDir.resolve(newFileName);


            Files.move(pending.getTempFilePath(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            sendMessage(chatId, "Фото сохранено!\nФайл: " + newFileName, null);

            pendingPhotos.remove(chatId);

            sendTextMessage("Выбирете действие: ", saveButtons, chatId);
//            userStates.remove(chatId);

        } catch (Exception e) {
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

    public void sendTextMessage(String text, Map<String, String> buttons, Long chatId) {
        try {
            SendMessage message = createMessage(text, buttons, chatId);
            var task = sendApiMethodAsync(message);
            this.sendMessages.add(task.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SendMessage createMessage(String text, Map<String, String> buttons, Long chatId) {
        SendMessage message = createMessage(text, chatId);
        if (buttons != null && !buttons.isEmpty())
            attachButtons(message, buttons);
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
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH));
             Workbook operationLog = new XSSFWorkbook(new FileInputStream(OPERATION_LOG_PATH));
             FileOutputStream fileOut = new FileOutputStream(PLAN_OTO_PATH)) {
            int lastRowNumber = operationLog.getSheetAt(1).getLastRowNum();
            Sheet workSheet = isIikLog ? planOTOWorkbook.getSheet("ИИК") : planOTOWorkbook.getSheet("ИВКЭ");
            int meterNumberColumnIndex = findColumnIndex(workSheet, "Номер счетчика");
            int lastColumnNumber = findColumnIndex(workSheet, "Отчет бригады о выполнении ОТО");
//            int stationColumnIndex = findColumnIndex(iikSheet, "Железнодорожная станция");
//            int substationColumnIndex = findColumnIndex(iikSheet, "ТП/КТП");

            CellStyle commonCellStyle = createCommonCellStyle(operationLog);
            CellStyle dateCellStyle = createDateCellStyle(operationLog);

            for (Row row : workSheet) {
                String meterNumber = getCellStringValue(row.getCell(meterNumberColumnIndex));
                String logData = opLog.getOrDefault(meterNumber, "");
                if (!logData.isEmpty()) {
                    Row newRow = operationLog.getSheetAt(1).createRow(lastRowNumber);
                    copyRow(row, newRow, lastColumnNumber, commonCellStyle, dateCellStyle);
                    addOtoData(logData, newRow);
//                    setRowStyle(commonCellStyle, dateCellStyle);
                }
            }
            operationLog.write(fileOut);

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
    }

    private void addOtoData(String logData, Row newRow) {
        switch (logData) {
            case "WK" -> {
                newRow.getCell(16).setCellValue(formattedCurrentDate);
                newRow.getCell(17).setCellValue("Нет связи со счетчиком");
                newRow.getCell(18).setCellValue("Уточнение реквизитов ТУ (подана заявка на корректировку НСИ)");
                newRow.getCell(19).setCellValue("");
                newRow.getCell(20).setCellValue("Исполнитель");
                newRow.getCell(21).setCellValue(formattedCurrentDate + " - Сброшена ошибка ключа Вронгкей (счетчик не на связи)");
            }
            case "meterChange" -> log.info("meterChange");
        }
    }

    private static void copyRow(Row sourceRow, Row targetRow, int columnCount, CellStyle defaultCellStyle, CellStyle dateCellStyle) {
        for (int i = 0; i < columnCount; i++) {
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
            for (Row row : iikSheet) {
                String meterNum = getCellStringValue(row.getCell(meterNumberColumnIndex));
                if (meterNum != null) {
                    paths.put(meterNum,
                            eelToNtel.get(row.getCell(eelColumnIndex).getStringCellValue()) + "\\" +
                                    row.getCell(stationColumnIndex).getStringCellValue() + "\\" +
                                    row.getCell(substationColumnIndex).getStringCellValue());
                }
            }
        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }
        return paths;
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

    private static CellStyle createDateCellStyle(Workbook resultWorkbook) {
        CellStyle dateCellStyle = resultWorkbook.createCellStyle();
        DataFormat dateFormat = resultWorkbook.createDataFormat(); // Формат даты
        dateCellStyle.setDataFormat(dateFormat.getFormat("dd.MM.YYYY"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setFont(createCellFontStyle(resultWorkbook, "Calibri", (short) 10, false));
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
