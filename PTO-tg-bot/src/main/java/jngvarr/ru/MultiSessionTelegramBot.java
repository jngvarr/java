package jngvarr.ru;



import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiSessionTelegramBot extends TelegramLongPollingBot {
    private String name;
    private String token;

    private ThreadLocal<Update> updateEvent = new ThreadLocal<>();
    private HashMap<Long, Integer> gloryStorage = new HashMap<>();

    private List<Message> sendMessages = new ArrayList<>();

    public MultiSessionTelegramBot(String name, String token) {
        this.name = name;
        this.token = token;
    }

    static final Logger logger = LoggerFactory.getLogger(PtoTelegramBot.class);

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public final void onUpdateReceived(Update updateEvent) {
        this.updateEvent.set(updateEvent);
        onUpdateEventReceived(this.updateEvent.get());
    }

    public void onUpdateEventReceived(Update updateEvent) {
        //do nothing
    }

    public String getUsername(Update updateEvent) {
        if (updateEvent.hasMessage()) {
            return updateEvent.getMessage().getFrom().getUserName();
        } else if (updateEvent.hasCallbackQuery()) {
            return updateEvent.getCallbackQuery().getFrom().getUserName();
        }
        return "User is absent";
    }

    public Long getCurrentChatId() {
        if (updateEvent.get().hasMessage()) {
            return updateEvent.get().getMessage().getFrom().getId();
        }

        if (updateEvent.get().hasCallbackQuery()) {
            return updateEvent.get().getCallbackQuery().getFrom().getId();
        }

        return null;
    }

    public String getMessageText() {
        return updateEvent.get().hasMessage() ? updateEvent.get().getMessage().getText() : "";
    }

    public String getCallbackQueryButtonKey() {
        return updateEvent.get().hasCallbackQuery() ? updateEvent.get().getCallbackQuery().getData() : "";
    }

    public void sendTextMessageAsync(String text) {
        try {
            SendMessage message = createMessage(text);
            var task = sendApiMethodAsync(message);
            this.sendMessages.add(task.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTextMessageAsync(String text, Map<String, String> buttons) {
        try {
            SendMessage message = createMessage(text, buttons);
            var task = sendApiMethodAsync(message);
            this.sendMessages.add(task.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPhotoMessageAsync(String photoKey) {
        SendPhoto photo = createPhotoMessage(photoKey);
        executeAsync(photo);
    }

    public void sendImageMessageAsync(String imagePath) {
        SendPhoto photo = createPhotoMessage(Path.of(imagePath));
        executeAsync(photo);
    }

    public SendMessage createMessage(String text) {
        SendMessage message = new SendMessage();
        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        Long chatId = getCurrentChatId();
        message.setChatId(chatId);
        return message;
    }

    public SendMessage createMessage(String text, Map<String, String> buttons) {
        SendMessage message = createMessage(text);
        if (buttons != null && !buttons.isEmpty())
            attachButtons(message, buttons);
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

    public SendPhoto createPhotoMessage(String name) {
        try {
            var is = ClassLoader.getSystemResourceAsStream("images/" + name + ".jpg");
            return createPhotoMessage(is);
        } catch (Exception e) {
            throw new RuntimeException("Can't create photo message!");
        }
    }

    public SendPhoto createPhotoMessage(Path path) {
        try {
            var is = Files.newInputStream(path);
            return createPhotoMessage(is);
        } catch (IOException e) {
            throw new RuntimeException("Can't create image message!");
        }
    }

    public Message getLastSentMessage() {
        if (this.sendMessages.isEmpty()) return null;
        return this.sendMessages.get(this.sendMessages.size() - 1);
    }

    public List<Message> getAllSentMessages() {
        return this.sendMessages;
    }

    public void editTextMessageAsync(Integer messageId, String text) {
        try {
            EditMessageText command = new EditMessageText();
            command.setChatId(getCurrentChatId());
            command.setMessageId(messageId);
            command.setText(text);
            executeAsync(command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private SendPhoto createPhotoMessage(InputStream inputStream) {
        try {
            InputFile inputFile = new InputFile();
            inputFile.setMedia(inputStream, name);

            SendPhoto photo = new SendPhoto();
            photo.setPhoto(inputFile);
            Long chatId = getCurrentChatId();
            photo.setChatId(chatId);
            return photo;
        } catch (Exception e) {
            throw new RuntimeException("Can't create photo message!");
        }
    }

    public void handlePhotoMessage(Message message) {
        if (message.hasPhoto()) {
            List<PhotoSize> photos = message.getPhoto();
            PhotoSize largestPhoto = photos.get(photos.size() - 1); // Берём самую большую фотографию

            try {
                // Получаем объект File Telegram
                GetFile getFile = new GetFile(largestPhoto.getFileId());
                org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFile);
                String filePath = telegramFile.getFilePath();

                // Загружаем файл с серверов Telegram
                java.io.File downloadedFile = downloadFile(filePath);

                // Убедимся, что папка существует
                java.nio.file.Path saveDirectory = Paths.get("D:/Downloads/пто/tgphotoes");
                Files.createDirectories(saveDirectory);

                // Сохраняем загруженный файл на локальном диске
                String savePath = saveDirectory.resolve("photo_" + System.currentTimeMillis() + ".jpg").toString();
                Files.copy(downloadedFile.toPath(), Paths.get(savePath), StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Файл сохранён по пути: " + savePath);
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Сообщение не содержит фотографии.");
        }
    }


    private byte[] downloadFileAsByteArray(File file) throws TelegramApiException, IOException {
        // Получаем файл с серверов Telegram как поток данных
        InputStream inputStream = new FileInputStream(file.getFilePath());

        // Преобразуем InputStream в массив байтов
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close(); // Закрываем поток после чтения

        // Возвращаем массив байтов
        return byteArrayOutputStream.toByteArray();
    }


    public void clearBotCommands() {
        try {
            this.execute(new SetMyCommands(new ArrayList<>(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            logger.error("Ошибка при очистке команд: {}", e.getMessage());
        }
    }
}
