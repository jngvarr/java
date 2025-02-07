package jngvarr.ru.pto_ackye_rzhd.telegram;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import jngvarr.ru.pto_ackye_rzhd.config.BotConfig;
import jngvarr.ru.pto_ackye_rzhd.services.UserServiceImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Map<String, String>> modes = Map.of(
            "pto", Map.of(
                    "Проведение ПТО ИИК", "ptoIIK",
                    "Проведение ПТО ИВКЭ", "ptoIVKE"),
            "oto", Map.of(
                    "Проведение ОТО ИИК", "otoIIK",
                    "Проведение ОТО ИВКЭ", "otoIVKE"),
            "newTU", Map.of(
                    "Монтаж новой точки учёта", "addIIK",
                    "Демонтаж точки учёта", "delIIK",
                    "Монтаж концентратора", "dcMount",
                    "Демонтаж концентратора", "dcRemove"));

    // Карта для хранения состояния диалога по chatId
    private Map<Long, String> userStates = new HashMap<>();
    // Карта для хранения информации о фото, ожидающих подтверждения
    private Map<Long, PendingPhoto> pendingPhotos = new HashMap<>();


    public TBot(BotConfig config, UserServiceImpl service) {
        super(config.getBotToken());
        this.config = config;
        this.service = service;

//        modes.put("pto", Map.of())

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

        // Если фото не запрашивалось
        if (!userStates.containsKey(chatId)) {
            sendMessage(chatId, "Фото не запрашивалось. Если хотите начать, нажмите /start", null);
            return;
        }

        String currentState = userStates.get(chatId);
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

            // Сохраняем файл во временное хранилище
            java.io.File tempFile = java.io.File.createTempFile("photo_" + chatId + "_", ".jpg");
            try (InputStream in = new URL(fileUrl).openStream()) {
                Files.copy(in, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            // Считываем изображение
            BufferedImage bufferedImage = ImageIO.read(tempFile);
            if (bufferedImage == null) {
                sendMessage(chatId, "Не удалось обработать изображение.", null);
                userStates.remove(chatId);
                return;
            }

            // Декодируем штрих-код с помощью ZXing

            String barcodeText = decodeBarcode(bufferedImage);

            if (barcodeText == null) {
                BufferedImage grayImage = convertToGrayscale(bufferedImage);
                String barcodeTextFromGray = decodeBarcode(grayImage);
                if (barcodeTextFromGray == null) {
                    sendMessage(chatId, "Штрихкод не найден или не удалось его прочитать.\n Пожалуйста введите номер вручную: ", null);
                    Map<String, String> buttons = Map.of("Закончить загрузку", "LOADING_COMPLETE",
                            "Ввести номер вручную", "MANUAL_INSERT");
                    sendTextMessage("Выберите действие: ", buttons, null);
                    userStates.remove(chatId);
                    return;
                }
            }

            // Определяем тип фото в зависимости от состояния
            String type;
            if ("WAITING_FOR_PTOIIK_PHOTO".equals(currentState)) {
                type = "counter"; // для счетчика
            } else if ("WAITING_FOR_PTOIVKE_PHOTO".equals(currentState)) {
                type = "concentrator"; // для концентратора
            } else {
                type = "unknown";
            }

            // Сохраняем данные во временной структуре для последующей обработки
            pendingPhotos.put(chatId, new PendingPhoto(type, tempFile.toPath(), barcodeText));

            // Запрашиваем подтверждение: отправляем сообщение с inline-кнопками
            Map<String, String> buttons = Map.of(
                    "Да", "CONFIRM_BARCODE_YES",
                    "Нет", "CONFIRM_BARCODE_NO"
            );
            sendTextMessage("Считанный штрих-код: " + barcodeText + "\nЕсли он верный, нажмите «Да», иначе – «Нет» и введите корректное значение.", buttons, update);
        } catch (Exception e) {
            log.error("Ошибка обработки фото: " + e.getMessage());
            sendMessage(chatId, "Произошла ошибка при обработке фото.", null);
            userStates.remove(chatId);
        }
    }


    private void handleTextMessage(Update update) {
        String msgText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        // Если бот ожидает корректировки штрих-кода
        if ("WAITING_FOR_CORRECT_BARCODE".equals(userStates.get(chatId))) {
            PendingPhoto pending = pendingPhotos.get(chatId);
            if (pending != null) {
                pending.setScannedBarcode(msgText.trim());
                // После ввода корректного штрих-кода сохраняем фото
                savePhotoWithBarcode(chatId, pending);
                // Сбрасываем состояние ожидания корректировки
                userStates.remove(chatId);
            } else {
                sendMessage(chatId, "Нет ожидающих фото для коррекции.", null);
            }
            return;
        }
        if ("MANUAL_INSERT".equals(userStates.get(chatId))) {
            String deviceNumber = update.getMessage().getText();
        }

        // Обработка остальных текстовых сообщений
        switch (msgText) {
            case "/start" -> handleStartCommand(chatId, update.getMessage().getChat().getFirstName(), update);
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
                sendTextMessage("Что хотите сделать: ", modes.get("newTU"), update);
            }
            case "pto" -> {
                sendMessage(chatId, PtoTelegramBotContent.PTO, null);
                sendTextMessage("Что хотите сделать: ", modes.get("pto"), update);
            }
            case "oto" -> {
                sendMessage(chatId, PtoTelegramBotContent.OTO, null);
                sendTextMessage("Что хотите сделать: ", modes.get("oto"), update);
            }
            // Обработка выбора для счетчика и концентратора
            case "ptoIIK" -> {
                sendMessage(chatId, "Пожалуйста, загрузите фото счетчика.", null);
                userStates.put(chatId, "WAITING_FOR_PTOIIK_PHOTO");
            }
            case "ptoIVKE" -> {
                sendMessage(chatId, "Пожалуйста, загрузите фото концентратора.", null);
                userStates.put(chatId, "WAITING_FOR_PTOIVKE_PHOTO");
            }
            // Подтверждение считанного штрих-кода
            case "CONFIRM_BARCODE_YES" -> {
                PendingPhoto pending = pendingPhotos.get(chatId);
                if (pending != null) {
                    // Сохраняем фото с уже подтверждённым штрих-кодом
                    savePhotoWithBarcode(chatId, pending);
                } else {
                    sendMessage(chatId, "Нет ожидающих фото для подтверждения.", null);
                }
            }
            case "CONFIRM_BARCODE_NO" -> {
                // Устанавливаем состояние, что ожидается корректировка штрих-кода
                sendMessage(chatId, "Введите правильный штрих-код:", null);
                // Для этого можно, например, записать состояние в userStates:
                userStates.put(chatId, "WAITING_FOR_CORRECT_BARCODE");
            }

            case "LOADING_COMPLETE" -> {
                userStates.clear();
                sendMessage(chatId, "/start", null);
            }
            case "MANUAL_INSERT" -> {
                sendMessage(chatId, "Введите номер счетчика:", null);
                userStates.put(chatId, "MANUAL_INSERT");
            }

            default -> sendMessage(chatId, "Неизвестное действие. Попробуйте еще раз.", null);
        }
    }


    private void handleStartCommand(long chatId, String firstName, Update update) {
//        String welcomeMessage = String.format("Приветствую тебя, пользователь %s, Что будем делать?", firstName);
        log.info("Replied to user: {}", firstName);
//        sendMessage(chatId, welcomeMessage, null);

        sendTextMessage(MAIN_MENU, Map.of(
                "ПТО", "pto",
                "ОТО", "oto",
                "Монтаж / демонтаж ТУ", "newTU"
        ), update);
    }

    private void savePhotoWithBarcode(long chatId, PendingPhoto pending) {
        try {
            String baseDir = "photos" + java.io.File.separator + chatId;
            Path userDir = Paths.get(baseDir);
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }

            String prefix;
            if ("counter".equals(pending.getType())) {
                prefix = "ИИК_";
            } else if ("concentrator".equals(pending.getType())) {
                prefix = "ИВКЭ_";
            } else {
                prefix = "unknown_";
            }

            String newFileName = prefix + pending.getScannedBarcode() + ".jpg";
            Path destination = userDir.resolve(newFileName);

            Files.move(pending.getTempFilePath(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            sendMessage(chatId, "Фото получено и сохранено!\nШтрихкод: " + pending.getScannedBarcode() + "\nПуть: " + destination.toString(), null);
            // Удаляем данные из pendingPhotos
            pendingPhotos.remove(chatId);
            // Сбрасываем состояние ожидания, если оно ещё осталось
            userStates.remove(chatId);
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


    private void executeMessage(SendMessage message) {
        try {
            execute(message); // Отправляем сообщение в Telegram
        } catch (
                TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    public void sendTextMessage(String text, Map<String, String> buttons, Update update) {
        try {
            SendMessage message = createMessage(text, buttons, update);
            var task = sendApiMethodAsync(message);
            this.sendMessages.add(task.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SendMessage createMessage(String text, Map<String, String> buttons, Update update) {
        SendMessage message = createMessage(text, update);
        if (buttons != null && !buttons.isEmpty())
            attachButtons(message, buttons);
        return message;
    }

    public SendMessage createMessage(String text, Update update) {
        SendMessage message = new SendMessage();
        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        Long chatId = getCurrentChatId(update);
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

//    @Override
//    public String getBotUsername() {
//        return null;
//    }
//
//    @Override
//    public void onRegister() {
//        super.onRegister();
//    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
